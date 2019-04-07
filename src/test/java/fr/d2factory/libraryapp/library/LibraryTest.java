package fr.d2factory.libraryapp.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.MemberRepository;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class LibraryTest {
    private  Library library;
    private  BookRepository bookRepository;
    private  MemberRepository memberRepository;

    @BeforeEach
    public  void setup() throws IOException {

        bookRepository = new BookRepository();
        memberRepository=new MemberRepository();
        library = new LibraryImpl(bookRepository,memberRepository);

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
        NotAvailableBookException thrown = assertThrows(NotAvailableBookException.class, () ->library.borrowBook(46578964513L, resident, LocalDate.of(2019, 4, 5)));
        assertTrue(thrown.getMessage().contains("not available"));
    }

    @Test
    public void residents_are_taxed_10cents_for_each_day_they_keep_a_book() {
        Resident resident = new Resident();
        resident.setWallet(150);
        LocalDate borrowedAt=LocalDate.of(2019, 4, 5);
        Book book = library.borrowBook(46578964513L, resident, borrowedAt);
        long days= ChronoUnit.DAYS.between(borrowedAt,LocalDate.now());
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
        student.setWallet(10);
        student.setDateOfRegistration(LocalDate.of(2018, 9, 1));
        LocalDate borrowedAt=LocalDate.of(2019, 4, 5);
        Book book = library.borrowBook(46578964513L, student, borrowedAt);
        int days= (int)ChronoUnit.DAYS.between(borrowedAt,LocalDate.now());
        library.returnBook(book,student);
        int realDays=days-15;
        Float result;
        if(realDays>0) {
            result = 10 -  realDays* 0.10f;
        }else{
            result = 10f;
        }
        System.out.println("student"+realDays);
        assertEquals(student.getWallet(),result,0);
    }

    @Test
    public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days() {
        Student student = new Student();
        student.setWallet(100);
        student.setDateOfRegistration(LocalDate.of(2017, 9, 1));
        LocalDate borrowedAt=LocalDate.of(2019, 2, 5);
        Book book = library.borrowBook(46578964513L, student, borrowedAt);
        long days= ChronoUnit.DAYS.between(borrowedAt,LocalDate.now());
        library.returnBook(book,student);
        Float result = 100 -  ((30* 0.10f)+(days-30)*0.15f);
        System.out.println(days);
        System.out.println(student.getWallet());
        assertEquals(student.getWallet(),result,0);
    }

    @Test
    public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days() {
        Resident resident = new Resident();
        resident.setWallet(100);
        LocalDate borrowedAt=LocalDate.of(2019, 2, 5);
        Book book = library.borrowBook(46578964513L, resident, borrowedAt);
        long days= ChronoUnit.DAYS.between(borrowedAt,LocalDate.now());
        library.returnBook(book,resident);
        Float result = 100 -  60* 0.10f-(days-60)*0.20f;
        assertEquals(resident.getWallet(),result,0);

    }

    @Test
    public void members_cannot_borrow_book_if_they_have_late_books() {
        Resident resident = new Resident();
        resident.setWallet(100);
        LocalDate borrowedAt=LocalDate.of(2019, 2, 5);
        Book book = library.borrowBook(46578964513L, resident, borrowedAt);
        HasLateBooksException thrown = assertThrows(HasLateBooksException.class, () ->library.borrowBook(3326456467846L, resident, LocalDate.of(2019, 4, 5)));
        assertTrue(thrown.getMessage().contains("you are late"));
    }
}
