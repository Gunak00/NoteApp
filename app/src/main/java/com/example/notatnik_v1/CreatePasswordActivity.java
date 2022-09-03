package com.example.notatnik_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;

public class CreatePasswordActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[!*@#$%^&+=])" +     // co najmniej 1 znak specjalny
                    "(?=\\S+$)" +            // bez białych znakow
                    ".{4,}" +                // co najmniej 4 znaki
                    "$");
    private TextInputLayout password;
    private TextInputLayout passwordAgain;
    private String passwordInput, passwordAgainInput;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        getSupportActionBar().hide();

        password = findViewById(R.id.etPassword);
        passwordAgain = findViewById(R.id.etPasswordAgain);
        save = findViewById(R.id.btSavePassword);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(savePassword()){
                    new CDialog(CreatePasswordActivity.this).createAlert("Udało się :)", CDConstants.SUCCESS, CDConstants.LARGE)
                            .setAnimation(CDConstants.SLIDE_FROM_BOTTOM_TO_TOP)
                            .setDuration(1500)
                            .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                            .show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), EnterPasswordActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 1400);
                }
            }
        });
    }

    private boolean validatePassword(){
        passwordInput = password.getEditText().getText().toString();

        if (passwordInput.isEmpty()){
            password.setError("Pole nie może być puste!");
            return  false;
        }else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()){
            password.setError("Hasło jest zbyt słabe!\n" +
                    "Hasło musi: \n" +
                    "zawierać co najmniej 1 znak specjalny,\n" +
                    "nie mieć znaków białych,\n" +
                    "mieć co najmniej 4 znaki.");
            return  false;
        }else{
            password.setError(null);
            return  true;
        }
    }

    private boolean isEqualPasswords(){
        passwordAgainInput = passwordAgain.getEditText().getText().toString();
        if (passwordAgainInput.isEmpty()) {
            passwordAgain.setError("Pole nie może być puste!");
            return false;
        }else if (!passwordInput.equals(passwordAgainInput)){
            passwordAgain.setError("Hasła różnią się!");
            return false;
        }else{
            passwordAgain.setError(null);
            return true;
        }
    }

    private boolean savePassword(){
        if (validatePassword() && isEqualPasswords()){
            SharedPreferences settings = getSharedPreferences("PREFS", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("password", passwordInput);
            editor.apply();
            return  true;
        }else{
            return false;
        }
    }

}