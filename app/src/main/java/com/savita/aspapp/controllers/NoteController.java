package com.savita.aspapp.controllers;

import android.util.Log;

import com.savita.aspapp.configs.AppConfig;
import com.savita.aspapp.models.Note;
import com.savita.aspapp.models.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

public class NoteController {
    private static final String LOG_TAG = "NoteController_tag";
    public static Response<List<Note>> get(String authorizationToken) {
        try {
            return APIController.get(AppConfig.API_URL + "/notes", str -> {
                try {
                    return JSONParser.getResponseOfListsOfNotes(str);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, e.getMessage());
                } catch (ParseException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
                return null;
            }, authorizationToken);
        } catch(Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
            return null;
        }
    }

    public static Response<Note> create(Note note, String authorizationToken) {
        try {
            JSONObject jsonObj = note.getJSON();
            return APIController.post(AppConfig.API_URL + "/notes", jsonObj, str -> {
                try {
                    return JSONParser.getResponseOfNote(str);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, e.getMessage());
                } catch (ParseException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
                return null;
            }, authorizationToken);
        } catch(Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
            return null;
        }
    }

    public static Response<Note> update(Note note, String authorizationToken) {
        try {
            JSONObject jsonObj = note.getJSON();
            return APIController.put(AppConfig.API_URL + "/notes", jsonObj, str -> {
                try {
                    return JSONParser.getResponseOfNote(str);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, e.getMessage());
                } catch (ParseException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
                return null;
            }, authorizationToken);
        } catch(Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
            return null;
        }
    }

    public static Response<Boolean> remove(Note note, String authorizationToken) {
        try {
            return APIController.remove(AppConfig.API_URL + "/notes", note.getId(), str -> {
                try {
                    return JSONParser.getResponseOfBoolean(str);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
                return null;
            }, authorizationToken);
        } catch(Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
            return null;
        }
    }

}
