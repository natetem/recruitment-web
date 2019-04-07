package fr.d2factory.libraryapp.member;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Student extends Member {

    private LocalDate dateOfRegistration;
    private final static int FIRST_YEAR_FREE_DAYS = 15;
    private static int normalPeriod = 30;
    private final static float NORMAL_PRICE = 0.10f;
    private final static float LATE_PRICE = 0.15f;

    @Override
    public void payBook(int numberOfDays) {
        float money;
        numberOfDays = realNumberOfDay(numberOfDays);
        if (this.hasLateBook(numberOfDays)) {
            money = (numberOfDays - normalPeriod) * LATE_PRICE + normalPeriod * NORMAL_PRICE;
        } else {
            money = numberOfDays * NORMAL_PRICE;
        }
        this.setWallet(this.getWallet() - money);
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
        return ChronoUnit.YEARS.between(this.dateOfRegistration, LocalDate.now()) < 1;
    }

    public LocalDate getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(LocalDate dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    @Override
    public boolean hasLateBook(int numberOfDays) {
        return numberOfDays > normalPeriod;
    }

}
