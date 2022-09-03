package com.example.notatnik_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;
import com.google.android.material.textfield.TextInputLayout;

public class EnterPasswordActivity extends AppCompatActivity {

    private TextInputLayout password;
    private String pass;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        getSupportActionBar().hide();

        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        pass = settings.getString("password", "");
        password = findViewById(R.id.etEnterPassword);
        login = findViewById(R.id.btLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = password.getEditText().getText().toString();
                if (text.equals(pass)){
                    new CDialog(EnterPasswordActivity.this).createAlert("Zalogowano!", CDConstants.SUCCESS, CDConstants.LARGE)
                            .setAnimation(CDConstants.SLIDE_FROM_BOTTOM_TO_TOP)
                            .setDuration(2000)
                            .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                            .show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 1900);
                }else{
                    new CDialog(EnterPasswordActivity.this).createAlert("Hasło niepoprawne!", CDConstants.ERROR, CDConstants.LARGE)
                            .setAnimation(CDConstants.SLIDE_FROM_BOTTOM_TO_TOP)
                            .setDuration(2000)
                            .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                            .show();
                    password.setError("Podaj poprawne hasło.");
                }
            }
        });
    }
}