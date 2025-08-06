package com.example.expensetracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DBHelper;
import com.example.expensetracker.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private DBHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        initializeViews();
        initializeDatabase();
        setupClickListeners();
        handleIntentExtras();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }
    }

    private void initializeDatabase() {
        dbHelper = new DBHelper(this);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleIntentExtras() {
        // Pre-fill username if coming from registration
        String registeredUsername = getIntent().getStringExtra("registered_username");
        if (registeredUsername != null) {
            etUsername.setText(registeredUsername);
            etPassword.requestFocus();
        }
    }

    private void attemptLogin() {
        // Clear any previous errors
        etUsername.setError(null);
        etPassword.setError(null);

        // Get input values
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validation
        if (!validateInput(username, password)) {
            return;
        }

        // Attempt login
        boolean isValid = dbHelper.checkUser(username, password);

        if (isValid) {
            // Save login session
            int userId = dbHelper.getUserId(username);
            sessionManager.saveLogin(username, userId);

            Toast.makeText(this, "Login successful! Welcome back, " + username, Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
        } else {
            // Invalid credentials
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            etPassword.setText(""); // Clear password field
            etUsername.requestFocus();
        }
    }

    private boolean validateInput(String username, String password) {
        boolean isValid = true;

        // Username validation
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            isValid = false;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            if (isValid) etPassword.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Close the app when back is pressed on login screen
        finishAffinity();
    }
}