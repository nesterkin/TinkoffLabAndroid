import java.util.Date;

public class ApiResponse {
    private String base;
    private RateObject rates;
    private Date date;

    public RateObject getRateApi() {
        return this.rates;
    }

    public String getBaseApi() {
        return this.base;
    }

    public Date getDateApi() {
        return this.date;
    }
}