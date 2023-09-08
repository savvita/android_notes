package com.savita.aspapp.controllers;

import android.util.Log;

import com.savita.aspapp.configs.AppConfig;
import com.savita.aspapp.models.Note;
import com.savita.aspapp.models.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class TagController {
    private static final String LOG_TAG = "TagController_tag";
    public static Response<String> create(String tag, String authorizationToken) {
        try {
            JSONObject jsonObj = getTagJSON(tag);
            return APIController.post(AppConfig.API_URL + "/tags", jsonObj, str -> {
                try {
                    return JSONParser.getResponseOfString(str);
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

    private static JSONObject getTagJSON(String tag) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put ("Value", tag);
        return jsonObj;
    }
}
