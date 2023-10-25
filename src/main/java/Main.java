import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Класс для получения изображения APOD (Astronomy Picture of the Day) от NASA API и сохранения его в файл.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String token = readTokenFromFile("token.txt");

        // Дополнительные параметры запроса
        String parameters = "&date=2023-10-10";

        // Создаем настраиваемый HttpClient с определенными таймаутами и отключенными редиректами
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидания подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        // Создаем GET-запрос к NASA API для Astronomy Picture of the Day (APOD)
        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=" + token + parameters);
        CloseableHttpResponse response = httpClient.execute(request);
        String responseString = EntityUtils.toString(response.getEntity());
        JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
        String url = jsonObject.get("url").getAsString();
        System.out.println(url);

        // Формируем автоматически название для файла
        String[] arr = url.split("/");
        String fileName = arr[arr.length - 1];

        // Запрашиваем изображение по URL и сохраняем его в файл
        CloseableHttpResponse pictureResponse = httpClient.execute(new HttpGet(url));
        HttpEntity entity = pictureResponse.getEntity();
        // Сохраняем в файл
        FileOutputStream fos = new FileOutputStream(fileName);
        entity.writeTo(fos);
        fos.close();
        httpClient.close();
    }

    private static String readTokenFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine();
        }
    }
}