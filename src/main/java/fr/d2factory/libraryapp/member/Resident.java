package fr.d2factory.libraryapp.member;

public class Resident  extends Member{

    private final static int normalPeriod = 60;
    private final static float NORMAL_PRICE = 0.10f;
    private final static float LATE_PRICE = 0.20f;

    @Override
    public void payBook(int numberOfDays) {
        float money;
        if (this.hasLateBook(numberOfDays)) {
            money = (numberOfDays - normalPeriod) * LATE_PRICE + normalPeriod * NORMAL_PRICE;
        } else {
            money = numberOfDays * NORMAL_PRICE;
        }
        this.setWallet(this.getWallet() - money);
    }

    @Override
    public  boolean hasLateBook(int numberOfDays){
        return numberOfDays>normalPeriod;
    }

}
