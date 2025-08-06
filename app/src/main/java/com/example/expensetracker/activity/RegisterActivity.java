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

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        initializeDatabase();
        setupClickListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Register");
        }
    }

    private void initializeDatabase() {
        dbHelper = new DBHelper(this);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegistration());

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptRegistration() {
        // Clear any previous errors
        etUsername.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);

        // Get input values
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validation
        if (!validateInput(username, password, confirmPassword)) {
            return;
        }

        // Attempt registration
        boolean success = dbHelper.insertUser(username, password);

        if (success) {
            Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();

            // Navigate to login screen
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra("registered_username", username); // Pre-fill username in login
            startActivity(intent);
            finish();
        } else {
            // Username already exists
            etUsername.setError("Username already exists. Please choose a different username.");
            etUsername.requestFocus();
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String username, String password, String confirmPassword) {
        boolean isValid = true;

        // Username validation
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            isValid = false;
        } else if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters long");
            etUsername.requestFocus();
            isValid = false;
        } else if (username.length() > 20) {
            etUsername.setError("Username must be less than 20 characters");
            etUsername.requestFocus();
            isValid = false;
        } else if (!username.matches("[a-zA-Z0-9_]+")) {
            etUsername.setError("Username can only contain letters, numbers, and underscores");
            etUsername.requestFocus();
            isValid = false;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            if (isValid) etPassword.requestFocus();
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters long");
            if (isValid) etPassword.requestFocus();
            isValid = false;
        }

        // Confirm password validation
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            if (isValid) etConfirmPassword.requestFocus();
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            if (isValid) etConfirmPassword.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onBackPressed() {
        // Navigate to login instead of closing app
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}