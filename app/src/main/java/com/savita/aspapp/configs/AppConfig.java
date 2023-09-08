package com.savita.aspapp.configs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppConfig {
    public static final String API_URL = "http://v4038722-001-site1.ctempurl.com/api";
    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_TOKEN = "token";
    public static final String APP_PREFERENCES_TAGS = "tags";
    public static final String NOTE_ID = "note_id";
    public static final String NOTE_TITLE = "note_title";
    public static final String NOTE_DATE = "note_date";
    public static final String NOTE_TEXT = "note_text";
    public static final String NOTE_TAGS = "note_tags";
    public static final DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy HH:mm:ss", Locale.ENGLISH);
    public static final int BUFFER_SIZE = 1024;
}
