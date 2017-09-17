import java.util.Date;

class ApiResponse {
    private String mBase;
    private Date mDate;
    private RateObject mRateObject;

    public ApiResponse(String base, Date date, RateObject rateObject) {
        mBase = base;
        mDate = date;
        mRateObject = rateObject;
    }

    String getBaseApi() {
        return mBase;
    }

    Date getDateApi() {
        return mDate;
    }

    RateObject getRateApi() {
        return mRateObject;
    }
}