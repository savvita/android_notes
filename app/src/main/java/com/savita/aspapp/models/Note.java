package com.savita.aspapp.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Note {
    private int id;
    private Date date;
    private String title;
    private String text;
    private List<String> tags;

    public Note() {
        tags = new ArrayList<>();
    }

    public Note(String title) {
        this.title = title;
        tags = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void prepare() {
        title = trim(title);
        text = trim(text);

        if(tags != null) {
            tags = tags.stream()
                    .filter(x -> x != null)
                    .map(x -> x.trim())
                    .filter(x -> x.length() > 0)
                    .collect(Collectors.toList());
        }
    }


    private String trim(String value) {
        return value != null ? value.trim() : null;
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject jsonObj = new JSONObject();
        if(id > 0) {
            jsonObj.put("Id", id);
        }
        jsonObj.put ("Title", title);
        jsonObj.put ("Text", text);

        JSONArray tagsArr = new JSONArray();

        if(tags != null) {
            tags.stream().forEach(x -> {
                JSONObject tagObj = new JSONObject();
                try {
                    tagObj.put("Value", x);
                } catch (JSONException e) {
                    Log.d("JSONParser_tag", e.getMessage());
                }
                tagsArr.put(tagObj);
            });
        }

        jsonObj.put("Tags", tagsArr);

        return jsonObj;
    }
}
