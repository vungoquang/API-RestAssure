package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class CommonUtils {
    public static String convertSecondToTimeFormat(long second) {
        long milliSeconds = second * 1000;
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(milliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(milliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(milliSeconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }

    public static String parseObjectArrayIntoString(Object[] array) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < array.length; i++) {
            String param = String.valueOf(array[i]);
            if (!param.isEmpty()) {
                result.append(param);
            }
            if (i != array.length - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public void writeJsonfile(Response response, String fileName) throws IOException {
        String responseBody = response.getBody().asString();
        FileWriter file = new FileWriter(fileName);
        file.write(responseBody);
        file.flush();
        file.close();
    }

}
