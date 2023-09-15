package com.savita.aspapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.savita.aspapp.configs.AppConfig;
import com.savita.aspapp.controllers.AuthController;
import com.savita.aspapp.models.Login;
import com.savita.aspapp.models.Register;
import com.savita.aspapp.models.Response;
import com.savita.aspapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends AppCompatActivity {
    private static final String LOG_TAG = "SignInActivity_tag";
    private TextInputEditText username;
    private TextInputEditText password;
    private Button signInBtn;
    private Button clearBtn;
    private Button switchToSignUpBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        username = findViewById(R.id.sign_in_username_text_input);
        password = findViewById(R.id.sign_in_password_text_input);
        signInBtn = findViewById(R.id.sign_in_btn);
        clearBtn = findViewById(R.id.sign_in_clear_btn);
        switchToSignUpBtn = findViewById(R.id.switch_to_sign_up_btn);

        signInBtn.setOnClickListener(view -> login());
        clearBtn.setOnClickListener(view -> clearFields());
        switchToSignUpBtn.setOnClickListener(view -> switchToSignUp());
    }


    private void login() {
        List<String> errors = checkInputs();

        if(errors.size() > 0) {
            Toast.makeText(this, String.join("\n", errors), Toast.LENGTH_LONG).show();
            return;
        }

        Login model = new Login(username.getText().toString(), password.getText().toString());
        model.prepare();
        new Thread(() -> {
            Response<User> response = AuthController.login(model);
            if(response == null) {
                signInBtn.post(() -> Toast.makeText(this, R.string.authorization_failed, Toast.LENGTH_LONG).show());
            } else {
                signInBtn.post(() -> {
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                    clearFields();

                    SharedPreferences settings = getSharedPreferences(AppConfig.APP_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(AppConfig.APP_PREFERENCES_TOKEN, response.getToken());
                    if(response.getValue().getTags() != null && response.getValue().getTags().size() > 0) {
                        editor.putString(AppConfig.APP_PREFERENCES_TAGS, String.join(";", response.getValue().getTags()));
                    }
                    editor.apply();

                    Intent intent = new Intent(this, NotesListActivity.class);
                    startActivity(intent);
                });
            }
        }).start();

    }

    private List<String> checkInputs() {
        List<String> errors = new ArrayList<>();

        if(username.getText() == null || username.getText().toString().trim().length() == 0) {
            errors.add(getResources().getString(R.string.required_username));
        }

        if(password.getText() == null || password.getText().toString().trim().length() == 0) {
            errors.add(getResources().getString(R.string.required_password));
        }

        return errors;
    }

    private void switchToSignUp() {
        clearFields();
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void clearFields() {
        username.setText("");
        password.setText("");
    }
}