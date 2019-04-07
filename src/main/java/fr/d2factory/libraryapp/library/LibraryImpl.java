package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;
import sun.invoke.empty.Empty;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public class LibraryImpl implements Library {
    private BookRepository bookRepository;

    public LibraryImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException{
        Book book= this.bookRepository.findBook(isbnCode);
        Optional<LocalDate> alreadyBorrow=Optional.ofNullable(this.bookRepository.findBorrowedBookDate(book));
        if(!alreadyBorrow.isPresent()){
            this.bookRepository.saveBookBorrow(book,borrowedAt);
            return book;
        }
        return null;
    }

    public void returnBook(Book book, Member member){
        LocalDate borrowedAt=this.bookRepository.findBorrowedBookDate(book);
        int numberOfDays= Period.between(borrowedAt,LocalDate.now()).getDays();
        member.payBook(numberOfDays);
        this.bookRepository.returnBorrowedBook(book);
    }
}
