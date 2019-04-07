package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.library.HasLateBooksException;

public class Resident  extends Member{


    private final static int NORMAL_PERIOD = 60;
    private final static float NORMAL_PRICE = 0.10f;
    private final static float LATE_PRICE = 0.20f;

    @Override
    public void payBook(int numberOfDays) {
        float money;
        if (numberOfDays <= NORMAL_PERIOD) {
            money = numberOfDays * NORMAL_PRICE;
            this.setWallet(this.getWallet() - money);
        } else {
            money = (numberOfDays - NORMAL_PERIOD) * LATE_PRICE + NORMAL_PERIOD * numberOfDays;
            this.setWallet(this.getWallet() - money);
            throw new HasLateBooksException("your  late");
        }

    }
}
