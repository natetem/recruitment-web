package fr.d2factory.libraryapp.library;

public class NotAvailableBookException extends RuntimeException {

    public NotAvailableBookException(String message) {
        super(message);
    }
}
