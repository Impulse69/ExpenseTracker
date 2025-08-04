package com.example.expensetracker.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.adapter.CategoryAdapter;
import com.example.expensetracker.database.DBHelper;
import com.example.expensetracker.model.Category;
import java.util.List;
import java.util.Random;

public class CategoriesActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryDeleteListener {

    private RecyclerView categoriesRecyclerView;
    private Button addCategoryBtn;
    private DBHelper dbHelper;
    private CategoryAdapter categoryAdapter;
    private String[] predefinedColors = {
            "#FF5722", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
            "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
            "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800",
            "#FF5722", "#795548", "#9E9E9E", "#607D8B"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        initializeViews();
        initializeDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadCategories();
    }

    private void initializeViews() {
        categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
        addCategoryBtn = findViewById(R.id.add_category_btn);

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Categories");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeDatabase() {
        dbHelper = new DBHelper(this);
    }

    private void setupRecyclerView() {
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoriesRecyclerView.setHasFixedSize(true);
    }

    private void setupClickListeners() {
        addCategoryBtn.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void loadCategories() {
        List<Category> categories = dbHelper.getAllCategories();

        if (categoryAdapter == null) {
            categoryAdapter = new CategoryAdapter(this, categories);
            categoryAdapter.setOnCategoryDeleteListener(this);
            categoriesRecyclerView.setAdapter(categoryAdapter);
        } else {
            categoryAdapter.updateCategories(categories);
        }
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Category");

        // Create input field
        final EditText input = new EditText(this);
        input.setHint("Category name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                addNewCategory(categoryName);
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addNewCategory(String categoryName) {
        // Check if category already exists
        List<Category> existingCategories = dbHelper.getAllCategories();
        for (Category category : existingCategories) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Get random color
        Random random = new Random();
        String randomColor = predefinedColors[random.nextInt(predefinedColors.length)];

        // Create and add category
        Category newCategory = new Category(categoryName, randomColor);
        long result = dbHelper.addCategory(newCategory);

        if (result != -1) {
            Toast.makeText(this, "Category added successfully!", Toast.LENGTH_SHORT).show();
            loadCategories(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteCategory(int categoryId, String categoryName) {
        // Check if category has expenses
        double categoryTotal = dbHelper.getCategoryTotal(categoryName);

        if (categoryTotal > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Cannot Delete Category")
                    .setMessage("This category has expenses associated with it. Please delete or move the expenses first.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete \"" + categoryName + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteCategory(categoryId);
                    Toast.makeText(this, "Category deleted successfully!", Toast.LENGTH_SHORT).show();
                    loadCategories(); // Refresh the list
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}