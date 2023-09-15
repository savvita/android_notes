package com.savita.aspapp.controllers;

import android.util.Log;

import com.savita.aspapp.configs.AppConfig;
import com.savita.aspapp.models.Login;
import com.savita.aspapp.models.Register;
import com.savita.aspapp.models.Response;
import com.savita.aspapp.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthController {
    private static final String LOG_TAG = "AuthController_tag";
    public static Response<User> register(Register model) {
        try {
            JSONObject jsonObj = model.getJSON();

            return APIController.post(AppConfig.API_URL + "/auth/register", jsonObj, (str) -> {
                try {
                    return JSONParser.getResponseOfUser(str);
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage());
                }
                return null;
            }, null);
        } catch(Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
            return null;
        }
    }

    public static Response<User> login(Login model) {
        try {
            JSONObject jsonObj = model.getJSON();
            return APIController.post(AppConfig.API_URL + "/auth", jsonObj, (str) -> {
                try {
                    return JSONParser.getResponseOfUser(str);
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                }
                return null;
            }, null);
        } catch(Exception ex) {
            Log.d(LOG_TAG, ex.getMessage(), ex);
            return null;
        }
    }
}
