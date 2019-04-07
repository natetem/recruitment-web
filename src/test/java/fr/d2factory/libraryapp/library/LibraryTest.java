package fr.d2factory.libraryapp.library;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class LibraryTest {
    private Library library;
    private BookRepository bookRepository;

    @Before
    public void setup() throws IOException {
        //TODO instantiate the library and the repository
        bookRepository = new BookRepository();
        library = new LibraryImpl(bookRepository);

        //TODO add some test books (use BookRepository#addBooks)
        //TODO to help you a file called books.json is available in src/test/resources
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(new File("src/test/resources/books.json"));
        List<Book> books = new ArrayList<>();
        for (JsonNode node : root) {
            String title = node.path("title").asText();
            String author = node.path("author").asText();
            Long isbnCode = node.path("isbn").path("isbnCode").asLong();
            Book book = new Book(title, author, new ISBN(isbnCode));
            books.add(book);

        }
        bookRepository.addBooks(books);

    }

    @Test
    public void member_can_borrow_a_book_if_book_is_available() {
        Student student = new Student();
        student.setWallet(150);
        student.setDateOfRegistration(LocalDate.of(2018, 9, 1));
        Book book = library.borrowBook(46578964513L, student, LocalDate.of(2019, 4, 5));
        assertEquals(book.getTitle(),"Harry Potter");
    }

    @Test
    public void borrowed_book_is_no_longer_available() {
        Student student = new Student();
        student.setWallet(150);
        student.setDateOfRegistration(LocalDate.of(2018, 9, 1));
        Book book1 = library.borrowBook(46578964513L, student, LocalDate.of(2019, 4, 5));
        Resident resident = new Resident();
        resident.setWallet(150);
        Book book2 = library.borrowBook(46578964513L, resident, LocalDate.of(2019, 4, 5));
        assertNull(book2);
    }

    @Test
    public void residents_are_taxed_10cents_for_each_day_they_keep_a_book() {
        Resident resident = new Resident();
        resident.setWallet(150);
        LocalDate borrowedAt=LocalDate.of(2019, 4, 5);
        Book book = library.borrowBook(46578964513L, resident, borrowedAt);
        int days= Period.between(borrowedAt,LocalDate.now()).getDays();
        library.returnBook(book,resident);
        Float result=150-days*0.10f;
        assertEquals(resident.getWallet(),result,0);

    }

    @Test
    public void students_pay_10_cents_the_first_30days() {
        Student student = new Student();
        student.setWallet(120);
        student.setDateOfRegistration(LocalDate.of(2017, 9, 1));
        LocalDate borrowedAt=LocalDate.of(2019, 4, 5);
        Book book = library.borrowBook(46578964513L, student, borrowedAt);
        int days= Period.between(borrowedAt,LocalDate.now()).getDays();
        library.returnBook(book,student);
        Float result=120-days*0.10f;
        assertEquals(student.getWallet(),result,0);
    }

    @Test
    public void students_in_1st_year_are_not_taxed_for_the_first_15days() {
        Student student = new Student();
        student.setWallet(120);
        student.setDateOfRegistration(LocalDate.of(2018, 9, 1));
        LocalDate borrowedAt=LocalDate.of(2019, 4, 5);
        Book book = library.borrowBook(46578964513L, student, borrowedAt);
        int days= Period.between(borrowedAt,LocalDate.now()).getDays();
        library.returnBook(book,student);
        int realDays=days-15;
        Float result;
        if(realDays>0) {
            result = 120 -  realDays* 0.10f;
        }else{
            result = 120f;
        }
        assertEquals(student.getWallet(),result,0);
    }

    @Test
    public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days() {
        Student student = new Student();
        student.setWallet(100);
        student.setDateOfRegistration(LocalDate.of(2017, 9, 1));
        LocalDate borrowedAt=LocalDate.of(2019, 3, 5);
        Book book = library.borrowBook(46578964513L, student, borrowedAt);
        int days= Period.between(borrowedAt,LocalDate.now()).getDays();
        library.returnBook(book,student);
        Float result = 100 -  30* 0.10f-(days-30)*0.15f;
        assertEquals(student.getWallet(),result,0);
    }

    @Test
    public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days() {
        Resident resident = new Resident();
        resident.setWallet(100);
        LocalDate borrowedAt=LocalDate.of(2019, 2, 5);
        Book book = library.borrowBook(46578964513L, resident, borrowedAt);
        int days= Period.between(borrowedAt,LocalDate.now()).getDays();
        library.returnBook(book,resident);
        Float result = 100 -  60* 0.10f-(days-60)*0.20f;
        assertEquals(resident.getWallet(),result,0);
    }

    @Test
    public void members_cannot_borrow_book_if_they_have_late_books() {
        fail("Implement me");
    }
}
