package com.excelmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String PASSWORD = "000";
    private static final String PREF_NAME = "ExcelManagerPrefs";
    private static final String PREF_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // تحقق إذا كان المستخدم مسجل دخول بالفعل
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(PREF_LOGGED_IN, false)) {
            startMainActivity();
        }

        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "أدخل كلمة السر!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.equals(PASSWORD)) {
                // حفظ حالة تسجيل الدخول
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PREF_LOGGED_IN, true);
                editor.apply();

                Toast.makeText(LoginActivity.this, "مرحبا بك!", Toast.LENGTH_SHORT).show();
                startMainActivity();
            } else {
                Toast.makeText(LoginActivity.this, "كلمة السر خاطئة! (الكود: 000)", Toast.LENGTH_SHORT).show();
                passwordInput.setText("");
            }
        });

        // زر تفريغ الحقل
        passwordInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                loginBtn.performClick();
                return true;
            }
            return false;
        });
    }

    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
