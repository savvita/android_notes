package com.savita.aspapp.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Login {
    private String username;
    private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void prepare() {
        username = trim(username);
        password = trim(password);
    }

    private String trim(String value) {
        return value != null ? value.trim() : null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Login{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put ("UserName", username);
        jsonObj.put ("Password", password);
        return jsonObj;
    }
}

