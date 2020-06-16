package com.nooty.nootylivenoots.messaging;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nooty.nootylivenoots.models.viewmodels.BaseRequestDto;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public abstract class RestServiceBase {
    private final Gson gson = new Gson();

    private <T> T executeRequest(HttpUriRequest request, Class<T> clazz) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request);) {
            HttpEntity entity = response.getEntity();
            final String entityString = EntityUtils.toString(entity);
            return gson.fromJson(entityString, clazz);
        } catch (IOException | JsonSyntaxException e) {
            System.out.println(e);
            return null;
        }
    }

    public <T> T executeQueryPost(BaseRequestDto request, String queryPost, Class<T> clazz) {

        // Build the query for the REST service
        final String query = queryPost;

        // Perform the query
        HttpPost httpPost = new HttpPost(query);
        httpPost.addHeader("content-type", "application/json");

        StringEntity params;
        try {
            String json = gson.toJson(request);
            params = new StringEntity(json);
            httpPost.setEntity(params);
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        }

        return executeRequest(httpPost, clazz);
    }

    public <T> T executeQueryGet(String queryGet, Class<T> clazz) {

        // Build the query for the REST service
        final String query = queryGet;
        // Perform the query
        HttpGet httpGet = new HttpGet(query);

        return executeRequest(httpGet, clazz);

    }

    public <T> T executeQueryPut(BaseRequestDto petRequest, String queryPut, Class<T> clazz) {

        // Build the query for the REST service
        final String query = queryPut;

        // Perform the query
        HttpPut httpPut = new HttpPut(query);
        httpPut.addHeader("content-type", "application/json");
        StringEntity params;
        try {
            params = new StringEntity(gson.toJson(petRequest));
            httpPut.setEntity(params);
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        }

        return executeRequest(httpPut, clazz);
    }
}
