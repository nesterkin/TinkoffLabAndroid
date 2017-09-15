import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.*;

public class Application {
    private static double DownloadFromInternet(String strURL) {
        try {
            URL url = new URL(strURL);
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
            //e.printStackTrace();
            System.out.println("Неверно введена валюта");
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Неверно введена валюта, отсутствует подключение к " +
                    "интернету или отказано в записи в кэш");
        }
        return 0;
    }

    private static boolean DateCheck(ApiResponse apiResponse) {
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

    private static double CacheCheck(String from, String to) {
        try {
            File cacheFile = new File("src/main/resources/CacheFile");
            if (cacheFile.exists()) {
                BufferedReader cacheReader = new BufferedReader(
                        new FileReader(cacheFile));
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(RateObject.class, new RatesDeserializer())
                        .create();
                ApiResponse apiResponse = gson.fromJson(cacheReader, ApiResponse.class);
                boolean check = DateCheck(apiResponse);
                //System.out.println("Данные свежие? " + updateNecessity);
                if (apiResponse != null) {
                    check &= apiResponse.getBaseApi().equals(from)
                            & apiResponse.getRateApi().getName().equals(to);
                    //System.out.println("Валюты совпали? " + updateNecessity);
                    return check ? apiResponse.getRateApi().getRate() : 0;
                }
            } else return 0;
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Нет кэш файла");
        } catch (JsonSyntaxException e) {
            //e.printStackTrace();
            System.out.println("Кэш файл поврежден");
        }
        return 0;
    }

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder("http://api.fixer.io/latest?base=");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter from currency:");
        String from = scanner.nextLine().toUpperCase();
        stringBuilder.append(from);
        stringBuilder.append("&symbols=");
        System.out.println("Enter to currency:");
        String to = scanner.nextLine().toUpperCase();
        stringBuilder.append(to);

        if (to.equals(from)) {
            System.out.println("Вы ввели одинаковые валюты");
            return;
        }

        double rate = CacheCheck(from, to);

        if (rate == 0)
            rate = DownloadFromInternet(stringBuilder.toString());

        System.out.print(from + " => " + to + " : " + rate);
    }
}