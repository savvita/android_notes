package com.savita.aspapp.models;


public class Response<T> {
    private String token;
    private T value;
    private int hits;

    public Response() {
    }

    public Response(String token, T value, int hits) {
        this.token = token;
        this.value = value;
        this.hits = hits;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }
}
