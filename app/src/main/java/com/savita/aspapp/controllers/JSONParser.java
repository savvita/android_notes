package com.savita.aspapp.controllers;

import android.util.Log;

import com.savita.aspapp.configs.AppConfig;
import com.savita.aspapp.models.Note;
import com.savita.aspapp.models.Response;
import com.savita.aspapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {
    private static final String LOG_TAG = "JSONParser_tag";
    private JSONParser(){}
    public static User getUser(String json) throws JSONException {
        JSONObject objJson = new JSONObject(json);
        return getUser(objJson);
    }

    public static User getUser(JSONObject objJson) throws JSONException {
        User user = new User();
        user.setFirstName(objJson.getString("firstName"));
        user.setSecondName(objJson.getString("secondName"));
        user.setLastName(objJson.getString("lastName"));
        user.setUsername(objJson.getString("userName"));
        return user;
    }

    public static Note getNote(JSONObject objJson) throws JSONException, ParseException {
        Note note = new Note();
        note.setId(objJson.getInt("id"));
        note.setTitle(objJson.getString("title"));
        note.setText(objJson.getString("text"));

        String dateStr = objJson.getString("date");

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD'T'HH:mm:ss.SSSSSSS");
        java.util.Date date = formatter.parse(dateStr);
        note.setDate(date);

        JSONArray tagsObj = objJson.getJSONArray("tags");
        note.getTags().addAll(getListOfTags(tagsObj));
        return note;
    }

    public static Response<Note> getResponseOfNote(String json) throws JSONException, ParseException {
        Response<Note> response = new Response<>();
        JSONObject objJson = new JSONObject(json);

        Log.d(LOG_TAG, json);

        response.setHits(objJson.getInt("hits"));
        response.setToken(objJson.getString("token"));
        JSONObject valueObj = objJson.getJSONObject("value");
        response.setValue(getNote(valueObj));

        return response;
    }

    public static Response<String> getResponseOfString(String json) throws JSONException, ParseException {
        Response<String> response = new Response<>();
        JSONObject objJson = new JSONObject(json);

         response.setHits(objJson.getInt("hits"));
        response.setToken(objJson.getString("token"));
        JSONObject valueObj = objJson.getJSONObject("value");
        response.setValue(valueObj.getString("value"));

        return response;
    }

    public static Response<List<Note>> getResponseOfListsOfNotes(String json) throws JSONException, ParseException {
        Response<List<Note>> response = new Response<>();
        JSONObject objJson = new JSONObject(json);

        Log.d(LOG_TAG, json);

        response.setHits(objJson.getInt("hits"));
        response.setToken(objJson.getString("token"));
        JSONArray valueObj = objJson.getJSONArray("value");

        response.setValue(getListOfNotes(valueObj));

        return response;
    }

    public static List<Note> getListOfNotes(JSONArray valueObj) throws JSONException, ParseException {
        List<Note> notes = new ArrayList<>();
        for(int i = 0; i < valueObj.length(); i++) {
            JSONObject noteJson = valueObj.getJSONObject(i);
            notes.add(getNote(noteJson));
        }
        return notes;
    }

    public static List<String> getListOfTags(JSONArray valueObj) throws JSONException, ParseException {
        List<String> tags = new ArrayList<>();
        for(int i = 0; i < valueObj.length(); i++) {
            tags.add(valueObj.getJSONObject(i).getString("value"));
        }
        return tags;
    }

    public static Response<User> getResponseOfUser(String json) throws JSONException {
        Response<User> response = new Response<>();
        JSONObject objJson = new JSONObject(json);

        Log.d(LOG_TAG, json);

        response.setHits(objJson.getInt("hits"));
        response.setToken(objJson.getString("token"));
        JSONObject valueObj = objJson.getJSONObject("value");
        response.setValue(getUser(valueObj));

        return response;
    }

    public static Response<Boolean> getResponseOfBoolean(String json) throws JSONException {
        Response<Boolean> response = new Response<>();
        JSONObject objJson = new JSONObject(json);

        Log.d(LOG_TAG, json);

        response.setHits(objJson.getInt("hits"));
        response.setToken(objJson.getString("token"));
        response.setValue(objJson.getBoolean("value"));

        return response;
    }
}
