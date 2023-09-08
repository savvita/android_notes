package com.savita.aspapp.controllers;

import android.util.Log;

import com.savita.aspapp.configs.AppConfig;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class APIController {
    private static final String LOG_TAG = "ApiController_tag";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private static HttpURLConnection getConnection(String path, String method,  String authorizationToken) throws Exception {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        if(authorizationToken != null) {
            connection.setRequestProperty("Authorization", "Bearer " + authorizationToken);
        }
        return connection;
    }
    public static <T> T post(String path, org.json.JSONObject body, Function<String, T> deserializer, String authorizationToken) throws Exception {
        HttpURLConnection connection = getConnection(path, POST, authorizationToken);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        try(OutputStream os = connection.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os)) {
            osw.write(body.toString());
            osw.flush();

        } catch(Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
        }

        int code = connection.getResponseCode();
        Log.d(LOG_TAG, String.valueOf(code));
        String msg = connection.getResponseMessage();
        Log.d(LOG_TAG, msg);

        T result = null;

        if(code == HttpURLConnection.HTTP_OK) {
            try (InputStream stream = connection.getInputStream()) {
                String json = getStringFromStream(stream);
                result = deserializer.apply(json);

            } catch(Exception ex) {
                Log.d(LOG_TAG, ex.getMessage());
            }
        } else {
            logError(connection);
        }

        return result;
    }

    public static <T> T put(String path, org.json.JSONObject body, Function<String, T> deserializer, String authorizationToken) throws Exception {
        HttpURLConnection connection = getConnection(path, PUT, authorizationToken);
        connection.setDoOutput(true);

        try(OutputStream os = connection.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os)) {
            osw.write(body.toString());
            osw.flush();

        } catch(Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
        }

        int code = connection.getResponseCode();
        Log.d(LOG_TAG, String.valueOf(code));
        String msg = connection.getResponseMessage();
        Log.d(LOG_TAG, msg);

        T result = null;

        if(code == HttpURLConnection.HTTP_OK) {
            try (InputStream stream = connection.getInputStream()) {
                String json = getStringFromStream(stream);
                result = deserializer.apply(json);

            } catch(Exception ex) {
                Log.d(LOG_TAG, ex.getMessage());
            }
        } else {
            logError(connection);
        }

        return result;
    }

    public static <T, U> U remove(String path, T id, Function<String, U> deserializer, String authorizationToken) throws Exception {
        HttpURLConnection connection = getConnection(path + "/" + id.toString(), DELETE, authorizationToken);

        int code = connection.getResponseCode();
        Log.d(LOG_TAG, String.valueOf(code));
        String msg = connection.getResponseMessage();
        Log.d(LOG_TAG, msg);

        U result = null;

        if(code == HttpURLConnection.HTTP_OK) {
            try (InputStream stream = connection.getInputStream()) {
                String json = getStringFromStream(stream);
                result = deserializer.apply(json);

            } catch(Exception ex) {
                Log.d(LOG_TAG, ex.getMessage());
            }
        } else {
            logError(connection);
        }

        return result;
    }

    public static <T, U> U get(String path, Function<String, U> deserializer, String authorizationToken) throws Exception {
        HttpURLConnection connection = getConnection(path, GET, authorizationToken);
        connection.setDoInput(true);

        int code = connection.getResponseCode();
        Log.d(LOG_TAG, String.valueOf(code));
        String msg = connection.getResponseMessage();
        Log.d(LOG_TAG, msg);

        U result = null;

        if(code == HttpURLConnection.HTTP_OK) {
            try (InputStream stream = connection.getInputStream()) {
                String json = getStringFromStream(stream);
                result = deserializer.apply(json);

            } catch(Exception ex) {
                Log.d(LOG_TAG, ex.getMessage());
            }
        } else {
            logError(connection);
        }

        return result;
    }

    private static void logError(HttpURLConnection connection) {
        try (InputStream stream = connection.getInputStream()) {
            String json = getStringFromStream(stream);
            Log.d(LOG_TAG, json);

        } catch(Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
        }
    }

    private static String getStringFromStream(InputStream stream) throws IOException {
        char[] buffer = new char[AppConfig.BUFFER_SIZE];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
            out.append(buffer, 0, numRead);
        }

        return out.toString();
    }
}
