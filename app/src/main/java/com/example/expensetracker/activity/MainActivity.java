package com.example.expensetracker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ExpenseAdapter;
import com.example.expensetracker.database.DBHelper;
import com.example.expensetracker.model.Expense;
import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView totalExpensesText, recentExpensesTitle;
    private RecyclerView recentExpensesRecyclerView;
    private Button addExpenseBtn, viewHistoryBtn, categoriesBtn;
    private DBHelper dbHelper;
    private ExpenseAdapter expenseAdapter;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        loadDashboardData(); // Refresh data when returning to dashboard
    }

    private void initializeViews() {
        totalExpensesText = findViewById(R.id.total_expenses_text);
        recentExpensesTitle = findViewById(R.id.recent_expenses_title);
        recentExpensesRecyclerView = findViewById(R.id.recent_expenses_recycler);
        addExpenseBtn = findViewById(R.id.add_expense_btn);
        viewHistoryBtn = findViewById(R.id.view_history_btn);
        categoriesBtn = findViewById(R.id.categories_btn);

        decimalFormat = new DecimalFormat("#.##");
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
    }
}