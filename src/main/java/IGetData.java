interface IGetData {

    double getDataFromCache(String from, String to);

    double getDataFromInternet(String from, String to);
}