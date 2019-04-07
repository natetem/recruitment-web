package fr.d2factory.libraryapp.book;

import jdk.nashorn.internal.objects.annotations.Function;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {
    private Map<ISBN, Book> availableBooks = new HashMap<>();
    private Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    public void addBooks(List<Book> books) {
        availableBooks = books.stream()
                .collect(Collectors.toMap(Book::getIsbn,book->book));
    }

    public Book findBook(long isbnCode) {
        if(!availableBooks.containsKey(new ISBN(isbnCode))){
           return null;
        }
        return availableBooks.get(new ISBN(isbnCode));
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt){
        borrowedBooks.put(book,borrowedAt);
    }

    public LocalDate findBorrowedBookDate(Book book) {
        return borrowedBooks.get(book);
    }

    public void returnBorrowedBook(Book book) {
        borrowedBooks.remove(book);
    }
}
