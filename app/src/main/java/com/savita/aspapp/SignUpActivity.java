package com.savita.aspapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.savita.aspapp.configs.AppConfig;
import com.savita.aspapp.controllers.AuthController;
import com.savita.aspapp.models.Register;
import com.savita.aspapp.models.Response;
import com.savita.aspapp.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText username;
    private TextInputEditText password;
    private TextInputEditText passwordConfirmation;
    private TextInputEditText firstName;
    private TextInputEditText secondName;
    private TextInputEditText lastName;
    private Button signUpBtn;
    private Button clearBtn;
    private Button switchToSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.sign_up_username_text_input);
        password = findViewById(R.id.sign_up_password_text_input);
        passwordConfirmation = findViewById(R.id.sign_up_password_confirmation_text_input);
        firstName = findViewById(R.id.first_name_text_input);
        secondName = findViewById(R.id.second_name_text_input);
        lastName = findViewById(R.id.last_name_text_input);
        signUpBtn = findViewById(R.id.sign_up_btn);
        clearBtn = findViewById(R.id.sign_up_clear_btn);
        switchToSignInBtn = findViewById(R.id.switch_to_sign_in_btn);

        switchToSignInBtn.setOnClickListener(view -> switchToSignIn());
        signUpBtn.setOnClickListener(view -> register());
        clearBtn.setOnClickListener(view -> clearFields());
    }

    private void switchToSignIn() {
        clearFields();
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void register() {
        List<String> errors = checkInputs();

        if(errors.size() > 0) {
            Toast.makeText(this, String.join("\n", errors), Toast.LENGTH_LONG).show();
            return;
        }

        Register model = new Register(username.getText().toString(), password.getText().toString());
        model.setFirstName(firstName.getText().toString());
        model.setSecondName(secondName.getText().toString());
        model.setLastName(lastName.getText().toString());
        model.prepare();
        new Thread(() -> {
            Response<User> response = AuthController.register(model);
            if(response == null) {
                signUpBtn.post(() -> Toast.makeText(this, R.string.registration_failed, Toast.LENGTH_LONG).show());
            } else {
                signUpBtn.post(() -> {
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                    clearFields();

                    SharedPreferences settings = getSharedPreferences(AppConfig.APP_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(AppConfig.APP_PREFERENCES_TOKEN, response.getToken());
                    if(response.getValue().getTags().size() > 0) {
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

        if(password.getText() != null &&
                passwordConfirmation.getText() != null &&
                !password.getText().toString().equals(passwordConfirmation.getText().toString())) {
            errors.add(getResources().getString(R.string.passwords_mismatch));
        }

        return errors;
    }

    private void clearFields() {
        username.setText("");
        password.setText("");
        passwordConfirmation.setText("");
        firstName.setText("");
        secondName.setText("");
        lastName.setText("");
    }
}