package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.library.HasLateBooksException;

import java.time.LocalDate;

public class Student extends Member {

    private LocalDate dateOfRegistration;
    private final static int FIRST_YEAR_FREE_DAYS = 15;
    private final static int NORMAL_PERIOD = 30;
    private final static float NORMAL_PRICE = 0.10f;
    private final static float LATE_PRICE = 0.15f;

    @Override
    public void payBook(int numberOfDays) {
        float money;
        numberOfDays = realNumberOfDay(numberOfDays);
        if (numberOfDays <= NORMAL_PERIOD) {
            money = numberOfDays * NORMAL_PRICE;
            this.setWallet(this.getWallet() - money);
        } else {
            money = (numberOfDays - NORMAL_PERIOD) * LATE_PRICE + NORMAL_PERIOD * numberOfDays;
            this.setWallet(this.getWallet() - money);
            throw new HasLateBooksException("your are late");
        }

    }

    private int realNumberOfDay(int numberOfDays) {
        if (this.isFirstYear()) {
            if (numberOfDays <= FIRST_YEAR_FREE_DAYS) {
                return 0;
            } else {
                return numberOfDays - FIRST_YEAR_FREE_DAYS;
            }
        }
        return numberOfDays;
    }


    public boolean isFirstYear() {
        return this.dateOfRegistration.getYear() == LocalDate.now().getYear();
    }

    public LocalDate getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(LocalDate dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }
}
