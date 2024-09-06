package net.tripitakas.ik.elasticsearch;

import com.google.gson.Gson;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;

public class IntegrationTests {


    @Test
    public void testTokenizeCase1_correctly() throws IOException {
        /*
        sendDeleteRequest("http://localhost:9200/test_index");
        sendPutRequest("http://localhost:9200/test_index", "{\n" +
                "  \"settings\": {\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"rushi_analyzer\": {\n" +
                "          \"tokenizer\": \"rs_max_word\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");
         */
        java.io.InputStream inputStream = getClass().getResourceAsStream("/1.txt");
        String url = "http://localhost:9200/index2/_doc";

        // Read file content
        String fileContent = readAllText(inputStream);
        Dictionary<String, String> bodyObject = new Hashtable<>();
        bodyObject.put("content_vmax", fileContent);
        bodyObject.put("reel_txt", fileContent);
        bodyObject.put("content_vchar", fileContent);
        // 把bodyObject转换为Json字符串

        int code = sendPostRequest( "http://localhost:9200/test_index/_doc", new Gson().toJson(bodyObject));
        assert  code == 201;

        Dictionary<String, String> bodyObject2 = new Hashtable<>();
        bodyObject2.put("analyzer", "rs_max_word");
        bodyObject2.put("text", fileContent);
        sendPostRequest("http://localhost:9200/index2/_analyze",new Gson().toJson(bodyObject2));
    }

    private static String readAllText(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private int sendPostRequest(String urlString, String content) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = content.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        String s  = outputStreamToString(connection.getOutputStream());

        int responseCode = connection.getResponseCode();
        return responseCode;
    }

    public static String outputStreamToString(OutputStream outputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) outputStream;
        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private void sendPutRequest(String urlString, String content) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = content.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("PUT Response Code: " + responseCode);
    }

    private String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        }
    }

    private void sendDeleteRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        System.out.println("DELETE Response Code: " + responseCode);
    }
}
