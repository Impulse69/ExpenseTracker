package com.example.expensetracker.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DBHelper;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText titleEdit, descriptionEdit, amountEdit, dateEdit;
    private Spinner categorySpinner, recurringTypeSpinner;
    private CheckBox recurringCheckbox;
    private Button updateExpenseBtn, deleteExpenseBtn, cancelBtn;
    private DBHelper dbHelper;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private int expenseId;
    private Expense currentExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        // Get expense ID from intent
        expenseId = getIntent().getIntExtra("expense_id", -1);
        if (expenseId == -1) {
            Toast.makeText(this, "Error: Invalid expense", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        initializeDatabase();
        setupSpinners();
        setupClickListeners();
        loadExpenseData();
    }

    private void initializeViews() {
        titleEdit = findViewById(R.id.title_edit);
        descriptionEdit = findViewById(R.id.description_edit);
        amountEdit = findViewById(R.id.amount_edit);
        dateEdit = findViewById(R.id.date_edit);
        categorySpinner = findViewById(R.id.category_spinner);
        recurringCheckbox = findViewById(R.id.recurring_checkbox);
        recurringTypeSpinner = findViewById(R.id.recurring_type_spinner);
        updateExpenseBtn = findViewById(R.id.update_expense_btn);
        deleteExpenseBtn = findViewById(R.id.delete_expense_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Expense");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeDatabase() {
        dbHelper = new DBHelper(this);
    }

    private void setupSpinners() {
        // Setup category spinner
        List<Category> categories = dbHelper.getAllCategories();
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Setup recurring type spinner
        String[] recurringTypes = {"Daily", "Weekly", "Monthly"};
        ArrayAdapter<String> recurringAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, recurringTypes);
        recurringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurringTypeSpinner.setAdapter(recurringAdapter);
    }

    private void setupClickListeners() {
        dateEdit.setOnClickListener(v -> showDatePicker());

        recurringCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recurringTypeSpinner.setEnabled(isChecked);
        });

        updateExpenseBtn.setOnClickListener(v -> updateExpense());

        deleteExpenseBtn.setOnClickListener(v -> showDeleteConfirmation());

        cancelBtn.setOnClickListener(v -> finish());
    }

    private void loadExpenseData() {
        currentExpense = dbHelper.getExpense(expenseId);
        if (currentExpense == null) {
            Toast.makeText(this, "Error: Expense not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate fields with current expense data
        titleEdit.setText(currentExpense.getTitle());
        descriptionEdit.setText(currentExpense.getDescription());
        amountEdit.setText(String.valueOf(currentExpense.getAmount()));
        dateEdit.setText(currentExpense.getDate());

        // Set category spinner selection
        ArrayAdapter<String> categoryAdapter = (ArrayAdapter<String>) categorySpinner.getAdapter();
        int categoryPosition = categoryAdapter.getPosition(currentExpense.getCategory());
        if (categoryPosition >= 0) {
            categorySpinner.setSelection(categoryPosition);
        }

        // Set recurring checkbox and type
        recurringCheckbox.setChecked(currentExpense.isRecurring());
        recurringTypeSpinner.setEnabled(currentExpense.isRecurring());

        if (currentExpense.isRecurring() && currentExpense.getRecurringType() != null) {
            ArrayAdapter<String> recurringAdapter = (ArrayAdapter<String>) recurringTypeSpinner.getAdapter();
            int recurringPosition = recurringAdapter.getPosition(currentExpense.getRecurringType());
            if (recurringPosition >= 0) {
                recurringTypeSpinner.setSelection(recurringPosition);
            }
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateEdit.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateExpense() {
        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        String amountStr = amountEdit.getText().toString().trim();
        String date = dateEdit.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        boolean isRecurring = recurringCheckbox.isChecked();
        String recurringType = isRecurring ? recurringTypeSpinner.getSelectedItem().toString() : "";

        // Validation
        if (title.isEmpty()) {
            titleEdit.setError("Title is required");
            titleEdit.requestFocus();
            return;
        }

        if (amountStr.isEmpty()) {
            amountEdit.setError("Amount is required");
            amountEdit.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                amountEdit.setError("Amount must be greater than 0");
                amountEdit.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            amountEdit.setError("Please enter a valid amount");
            amountEdit.requestFocus();
            return;
        }

        // Update expense
        currentExpense.setTitle(title);
        currentExpense.setDescription(description);
        currentExpense.setAmount(amount);
        currentExpense.setCategory(category);
        currentExpense.setDate(date);
        currentExpense.setRecurring(isRecurring);
        currentExpense.setRecurringType(recurringType);

        int result = dbHelper.updateExpense(currentExpense);

        if (result > 0) {
            Toast.makeText(this, "Expense updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (dialog, which) -> deleteExpense())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteExpense() {
        dbHelper.deleteExpense(expenseId);
        Toast.makeText(this, "Expense deleted successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}