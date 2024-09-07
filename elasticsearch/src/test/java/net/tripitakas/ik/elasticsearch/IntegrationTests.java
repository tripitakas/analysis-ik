package net.tripitakas.ik.elasticsearch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;

public class IntegrationTests {


    @Test
    public void testTokenizeCase1_correctly() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        /*
        sendDeleteRequest("http://127.0.0.1:9200/test_index");
        sendPutRequest("http://127.0.0.1:9200/test_index", "{\n" +
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
        /*
        java.io.InputStream inputStream = getClass().getResourceAsStream("/1.txt");
        // Read file content
        String fileContent = readAllText(inputStream);
        Dictionary<String, String> bodyObject = new Hashtable<>();
        bodyObject.put("content_vmax", fileContent);
        Response resp;
        resp = sendPostRequest( "http://127.0.0.1:9200/index2/_doc", gson.toJson(bodyObject));
        assert  resp.getStatusCode() == 201;
*/
        /*
        Dictionary<String, String> bodyObject2 = new Hashtable<>();
        bodyObject2.put("analyzer", "rs_max_word");
        fileContent="你好呀";
        bodyObject2.put("text", fileContent);
        resp = sendPostRequest("http://127.0.0.1:9200/index2/_analyze",gson.toJson(bodyObject2));
        assert resp.getStatusCode() == 200;
        System.out.println(resp.getContent());*/
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

    public static Response sendPostRequest(String url, String jsonInputString) throws IOException {
        // 创建HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建HttpPost请求
            HttpPost post = new HttpPost(url);
            // 设置请求头
            post.setHeader("Content-Type", "application/json");
            // 设置请求体
            post.setEntity(new StringEntity(jsonInputString));

            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return new Response(response.getStatusLine().getStatusCode(), responseBody);
            }
        }
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
    
    public static class Response
    {
        private int statusCode;
        private String content;
        
        public Response(int statusCode, String content)
        {
            this.statusCode = statusCode;
            this.content = content;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getContent() {
            return content;
        }
    }
}
