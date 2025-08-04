package com.example.expensetracker.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.adapter.ExpenseAdapter;
import com.example.expensetracker.database.DBHelper;
import com.example.expensetracker.model.Expense;
import java.text.DecimalFormat;
import java.util.List;

public class ViewHistoryActivity extends AppCompatActivity {

    private TextView totalExpensesText, expenseCountText;
    private RecyclerView expensesRecyclerView;
    private DBHelper dbHelper;
    private ExpenseAdapter expenseAdapter;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        initializeViews();
        initializeDatabase();
        setupRecyclerView();
        loadExpenseHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenseHistory(); // Refresh data when returning from edit
    }

    private void initializeViews() {
        totalExpensesText = findViewById(R.id.total_expenses_text);
        expenseCountText = findViewById(R.id.expense_count_text);
        expensesRecyclerView = findViewById(R.id.expenses_recycler_view);

        decimalFormat = new DecimalFormat("#.##");

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Expense History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeDatabase() {
        dbHelper = new DBHelper(this);
    }

    private void setupRecyclerView() {
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expensesRecyclerView.setHasFixedSize(true);
    }

    private void loadExpenseHistory() {
        List<Expense> expenses = dbHelper.getAllExpenses();

        // Update summary information
        double totalAmount = dbHelper.getTotalExpenses();
        totalExpensesText.setText("$" + decimalFormat.format(totalAmount));
        expenseCountText.setText(expenses.size() + " expenses");

        // Setup adapter
        if (expenseAdapter == null) {
            expenseAdapter = new ExpenseAdapter(this, expenses);
            expensesRecyclerView.setAdapter(expenseAdapter);
        } else {
            expenseAdapter.updateExpenses(expenses);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}