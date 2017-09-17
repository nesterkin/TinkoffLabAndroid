class RateObject {
    private String mName;
    private double mRate;

    RateObject(String name, double rate) {
        mName = name;
        mRate = rate;
    }

    double getRate() {
        return mRate;
    }

    String getName() {
        return mName;
    }
}