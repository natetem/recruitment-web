package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.MemberRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class LibraryImpl implements Library {
    private BookRepository bookRepository;
    private MemberRepository memberRepository;

    public LibraryImpl(BookRepository bookRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException {

        List<Book> books = this.memberRepository.findBooksByMembers(member);
        if (!books.isEmpty()) {
            boolean isLate = books.parallelStream()
                    .map(b -> this.bookRepository.findBorrowedBookDate(b))
                    .map(d -> (int) ChronoUnit.DAYS.between(d, LocalDate.now()))
                    .anyMatch(d -> member.hasLateBook(d));
            if (isLate) {
                throw new HasLateBooksException("you can not borrow, you are late");
            }
        }
        Book book = this.bookRepository.findBook(isbnCode);
        Optional<LocalDate> alreadyBorrow = Optional.ofNullable(this.bookRepository.findBorrowedBookDate(book));
        if (alreadyBorrow.isPresent()) {
            throw new NotAvailableBookException("book is not available");
        }
        this.bookRepository.saveBookBorrow(book, borrowedAt);
        this.memberRepository.saveMemberBorrow(book, member);
        return book;

    }

    @Override
    public void returnBook(Book book, Member member) {
        LocalDate borrowedAt = this.bookRepository.findBorrowedBookDate(book);
        int numberOfDays = (int) ChronoUnit.DAYS.between(borrowedAt, LocalDate.now());
        member.payBook(numberOfDays);
        this.bookRepository.returnBorrowedBook(book);
    }
}
