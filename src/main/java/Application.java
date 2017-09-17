import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.*;

public class Application implements ICheck, IGetData {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter from currency:");
        String from = scanner.nextLine().toUpperCase();
        System.out.println("Enter to currency:");
        String to = scanner.nextLine().toUpperCase();

        if (to.equals(from)) {
            System.out.println("Вы ввели одинаковые валюты");
            return;
        }

        Application application = new Application();
        double rate = application.getDataFromCache(from, to);

        if (rate == 0) {
            rate = application.getDataFromInternet(from, to);
        }

        System.out.print(from + " => " + to + " : " + rate);
    }

    public boolean dateCheck(ApiResponse apiResponse) {
        Calendar apiCalendar = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        if (apiResponse != null) {
            apiCalendar.setTime(apiResponse.getDateApi());
            apiCalendar.add(Calendar.HOUR_OF_DAY, 18);
            Calendar nowCalendarCET = Calendar.getInstance(TimeZone.getTimeZone("CET"));
            long diffHours = (nowCalendarCET.getTimeInMillis() - apiCalendar.getTimeInMillis())
                    / 3600000;
            return diffHours < 24;
        } else return false;
    }

    public double getDataFromCache(String from, String to) {
        try {
            File cacheFile = new File("src/main/resources/CacheFile");
            if (cacheFile.exists()) {
                BufferedReader cacheReader = new BufferedReader(new FileReader(cacheFile));
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(RateObject.class, new RatesDeserializer())
                        .create();
                ApiResponse apiResponse = gson.fromJson(cacheReader, ApiResponse.class);
                boolean check = dateCheck(apiResponse);

                if (apiResponse != null) {
                    check &= apiResponse.getBaseApi().equals(from)
                            & apiResponse.getRateApi().getName().equals(to);

                    return check ? apiResponse.getRateApi().getRate() : 0;
                }
            } else return 0;
        } catch (FileNotFoundException | JsonSyntaxException e ) {
            System.out.println("Нет кэш файла или кэш файл поврежден");
        }
        return 0;
    }

    public double getDataFromInternet(String from, String to) {

        StringBuilder uri = new StringBuilder("http://api.fixer.io/latest?base=");
        uri.append(from)
                .append("&symbols=")
                .append(to);
        try {
            URL url = new URL(uri.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String temp = bufferedReader.readLine();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(temp.getBytes())));
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(RateObject.class, new RatesDeserializer())
                    .create();
            ApiResponse apiResponse = gson.fromJson(bufferedReader, ApiResponse.class);
            double rate = apiResponse.getRateApi().getRate();

            FileWriter writer = new FileWriter("src/main/resources/CacheFile");
            writer.write(temp);
            writer.flush();
            urlConnection.disconnect();
            return rate;
        } catch (NullPointerException e) {
            System.out.println("Неверно введена валюта");
        } catch (IOException e) {
            System.out.println("Неверно введена валюта, отсутствует подключение к " +
                    "интернету или отказано в записи в кэш");
        }
        return 0;
    }
}