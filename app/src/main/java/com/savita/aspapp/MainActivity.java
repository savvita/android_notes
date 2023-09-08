package com.savita.aspapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.savita.aspapp.configs.AppConfig;

import java.time.Period;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        SharedPreferences settings = getSharedPreferences(AppConfig.APP_PREFERENCES, MODE_PRIVATE);
        if(settings.contains(AppConfig.APP_PREFERENCES_TOKEN)) {
            Intent intent = new Intent(this, NotesListActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }
}