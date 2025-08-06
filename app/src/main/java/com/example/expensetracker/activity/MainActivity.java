package com.example.expensetracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ExpenseAdapter;
import com.example.expensetracker.database.DBHelper;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.utils.SessionManager;
import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView totalExpensesText, recentExpensesTitle, welcomeText;
    private RecyclerView recentExpensesRecyclerView;
    private Button addExpenseBtn, viewHistoryBtn, categoriesBtn;
    private DBHelper dbHelper;
    private SessionManager sessionManager;
    private ExpenseAdapter expenseAdapter;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        initializeViews();
        initializeDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadDashboardData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if user is still logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        loadDashboardData(); // Refresh data when returning to dashboard
    }

    private void initializeViews() {
        totalExpensesText = findViewById(R.id.total_expenses_text);
        recentExpensesTitle = findViewById(R.id.recent_expenses_title);
        recentExpensesRecyclerView = findViewById(R.id.recent_expenses_recycler);
        addExpenseBtn = findViewById(R.id.add_expense_btn);
        viewHistoryBtn = findViewById(R.id.view_history_btn);
        categoriesBtn = findViewById(R.id.categories_btn);
        welcomeText = findViewById(R.id.welcome_text); // Optional welcome text

        decimalFormat = new DecimalFormat("#.##");

        // Setup action bar
        if (getSupportActionBar() != null) {
            String username = sessionManager.getUsername();
            getSupportActionBar().setTitle("Expense Tracker - " + username);
        }
    }

    private void initializeDatabase() {
        dbHelper = new DBHelper(this);
    }

    private void setupRecyclerView() {
        recentExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentExpensesRecyclerView.setHasFixedSize(true);
    }

    private void setupClickListeners() {
        addExpenseBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        viewHistoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewHistoryActivity.class);
            startActivity(intent);
        });

        categoriesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
            startActivity(intent);
        });
    }

    private void loadDashboardData() {
        // Load total expenses
        double totalExpenses = dbHelper.getTotalExpenses();
        totalExpensesText.setText("$" + decimalFormat.format(totalExpenses));

        // Load recent expenses (last 5)
        List<Expense> allExpenses = dbHelper.getAllExpenses();
        List<Expense> recentExpenses;

        if (allExpenses.size() > 5) {
            recentExpenses = allExpenses.subList(0, 5);
        } else {
            recentExpenses = allExpenses;
        }

        if (expenseAdapter == null) {
            expenseAdapter = new ExpenseAdapter(this, recentExpenses);
            recentExpensesRecyclerView.setAdapter(expenseAdapter);
        } else {
            expenseAdapter.updateExpenses(recentExpenses);
        }

        // Update recent expenses title
        recentExpensesTitle.setText("Recent Expenses (" + recentExpenses.size() + ")");

        // Update welcome message if welcomeText exists
        if (welcomeText != null) {
            String username = sessionManager.getUsername();
            welcomeText.setText("Welcome back, " + username + "!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            showLogoutConfirmation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        sessionManager.logout();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Show exit confirmation
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Cancel", null)
                .show();
    }
}