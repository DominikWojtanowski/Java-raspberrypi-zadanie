package com.dominik;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class PostSender {
    private static RestTemplate restTemplate = new RestTemplate();



    public void CheckError()
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://localhost:8082/lifebits/errors");
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);

            try {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                // Przetwarzanie odpowiedzi
                System.out.println("Status code: " + statusCode);
                System.out.println("Response body: " + responseBody);
            } finally {
                response.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void Post(int status, String message, int uptime) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", message);
        jsonObject.put("uptime", uptime);

        String json = jsonObject.toString();
        // Tworzenie klienta HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            // Tworzenie obiektu HttpPost z adresem URL
            HttpPost httpPost = new HttpPost("http://localhost:8082/lifebits");

            // Ustawianie nagłówków żądania
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

            // Tworzenie StringEntity z obiektem JSON
            StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);

            // Ustawianie treści żądania
            httpPost.setEntity(requestEntity);

            // Wykonanie żądania HTTP POST
            CloseableHttpResponse response = httpClient.execute(httpPost);
            response.close();
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
