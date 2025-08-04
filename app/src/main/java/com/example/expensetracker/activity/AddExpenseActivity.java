package com.example.expensetracker.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
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

public class AddExpenseActivity extends AppCompatActivity {

    private EditText titleEdit, descriptionEdit, amountEdit, dateEdit;
    private Spinner categorySpinner, recurringTypeSpinner;
    private CheckBox recurringCheckbox;
    private Button saveExpenseBtn, cancelBtn;
    private DBHelper dbHelper;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        initializeViews();
        initializeDatabase();
        setupSpinners();
        setupClickListeners();
        setCurrentDate();
    }

    private void initializeViews() {
        titleEdit = findViewById(R.id.title_edit);
        descriptionEdit = findViewById(R.id.description_edit);
        amountEdit = findViewById(R.id.amount_edit);
        dateEdit = findViewById(R.id.date_edit);
        categorySpinner = findViewById(R.id.category_spinner);
        recurringCheckbox = findViewById(R.id.recurring_checkbox);
        recurringTypeSpinner = findViewById(R.id.recurring_type_spinner);
        saveExpenseBtn = findViewById(R.id.save_expense_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
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

        // Initially hide recurring type spinner
        recurringTypeSpinner.setEnabled(false);
    }

    private void setupClickListeners() {
        dateEdit.setOnClickListener(v -> showDatePicker());

        recurringCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recurringTypeSpinner.setEnabled(isChecked);
        });

        saveExpenseBtn.setOnClickListener(v -> saveExpense());

        cancelBtn.setOnClickListener(v -> finish());
    }

    private void setCurrentDate() {
        dateEdit.setText(dateFormatter.format(calendar.getTime()));
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

    private void saveExpense() {
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

        // Create and save expense
        Expense expense = new Expense(title, description, amount, category, date, isRecurring, recurringType);
        long result = dbHelper.addExpense(expense);

        if (result != -1) {
            Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show();
        }
    }
}