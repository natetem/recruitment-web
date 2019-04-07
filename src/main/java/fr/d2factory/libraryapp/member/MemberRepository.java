package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.book.Book;

import java.util.*;

public class MemberRepository {

    private Map<Member, List<Book>> borrowedBookMembers = new HashMap<>();

    public List<Book> findBooksByMembers(Member member) {
        List<Book> results = borrowedBookMembers.get(member);
        if (results == null) {
            results = new ArrayList<>();
            borrowedBookMembers.put(member, results);
        }
        return results;
    }

    public void saveMemberBorrow(Book book, Member member) {
        List<Book> results = this.findBooksByMembers(member);
        results.add(book);
        borrowedBookMembers.put(member, results);
    }

}
