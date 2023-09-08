package com.savita.aspapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.jvm.Transient;

public class Register {
    private String userName;
    private String password;
    @Transient
    private String passwordConfirmation;
    private String firstName;
    private String secondName;
    private String lastName;

    public Register(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    public void prepare() {
        userName = trim(userName);
        password = trim(password);
        passwordConfirmation = trim(passwordConfirmation);
        firstName = trim(firstName);
        secondName = trim(secondName);
        lastName = trim(lastName);
    }

    private String trim(String value) {
        return value != null ? value.trim() : null;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Register{" +
                "username='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put ("UserName", userName);
        jsonObj.put ("Password", password);
        jsonObj.put ("FirstName", firstName);
        jsonObj.put ("SecondName", secondName);
        jsonObj.put ("LastName", lastName);
        return jsonObj;
    }
}
