package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.Category;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseTracker.db";
    private static final int DATABASE_VERSION = 2; // Updated version for users table

    // Expenses table
    private static final String TABLE_EXPENSES = "expenses";
    private static final String COL_EXPENSE_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_CATEGORY = "category";
    private static final String COL_DATE = "date";
    private static final String COL_IS_RECURRING = "is_recurring";
    private static final String COL_RECURRING_TYPE = "recurring_type";

    // Categories table
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COL_CATEGORY_ID = "id";
    private static final String COL_CATEGORY_NAME = "name";
    private static final String COL_CATEGORY_COLOR = "color";

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_CREATED_AT = "created_at";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create expenses table
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + COL_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " TEXT,"
                + COL_DESCRIPTION + " TEXT,"
                + COL_AMOUNT + " REAL,"
                + COL_CATEGORY + " TEXT,"
                + COL_DATE + " TEXT,"
                + COL_IS_RECURRING + " INTEGER,"
                + COL_RECURRING_TYPE + " TEXT" + ")";
        db.execSQL(CREATE_EXPENSES_TABLE);

        // Create categories table
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COL_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_CATEGORY_NAME + " TEXT UNIQUE,"
                + COL_CATEGORY_COLOR + " TEXT" + ")";
        db.execSQL(CREATE_CATEGORIES_TABLE);

        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USERNAME + " TEXT UNIQUE NOT NULL,"
                + COL_PASSWORD + " TEXT NOT NULL,"
                + COL_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Insert default categories
        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add users table for version 2
            String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                    + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_USERNAME + " TEXT UNIQUE NOT NULL,"
                    + COL_PASSWORD + " TEXT NOT NULL,"
                    + COL_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
            db.execSQL(CREATE_USERS_TABLE);
        }
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] defaultCategories = {"Food", "Transportation", "Entertainment", "Shopping", "Bills", "Healthcare", "Other"};
        String[] defaultColors = {"#FF5722", "#2196F3", "#FF9800", "#9C27B0", "#4CAF50", "#F44336", "#607D8B"};

        for (int i = 0; i < defaultCategories.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COL_CATEGORY_NAME, defaultCategories[i]);
            values.put(COL_CATEGORY_COLOR, defaultColors[i]);
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    // User Authentication Methods

    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback to plain text (not recommended for production)
        }
    }

    /**
     * Insert new user into database
     * @param username The username
     * @param password The password (will be hashed)
     * @return true if user was inserted successfully, false if username already exists
     */
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if username already exists
        Cursor cursor = db.query(TABLE_USERS, null, COL_USERNAME + "=?",
                new String[]{username}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return false; // Username already exists
        }
        cursor.close();

        // Insert new user
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, hashPassword(password));

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    /**
     * Check if user credentials are valid
     * @param username The username
     * @param password The password
     * @return true if credentials are valid, false otherwise
     */
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);

        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, hashedPassword}, null, null, null);

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return isValid;
    }

    /**
     * Check if username exists in database
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USERNAME + "=?",
                new String[]{username}, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    /**
     * Get user ID by username
     * @param username The username
     * @return user ID if found, -1 otherwise
     */
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                COL_USERNAME + "=?", new String[]{username}, null, null, null);

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return userId;
    }

    // Expense CRUD operations (existing methods remain unchanged)
    public long addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, expense.getTitle());
        values.put(COL_DESCRIPTION, expense.getDescription());
        values.put(COL_AMOUNT, expense.getAmount());
        values.put(COL_CATEGORY, expense.getCategory());
        values.put(COL_DATE, expense.getDate());
        values.put(COL_IS_RECURRING, expense.isRecurring() ? 1 : 0);
        values.put(COL_RECURRING_TYPE, expense.getRecurringType());

        long id = db.insert(TABLE_EXPENSES, null, values);
        db.close();
        return id;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES + " ORDER BY " + COL_DATE + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(0));
                expense.setTitle(cursor.getString(1));
                expense.setDescription(cursor.getString(2));
                expense.setAmount(cursor.getDouble(3));
                expense.setCategory(cursor.getString(4));
                expense.setDate(cursor.getString(5));
                expense.setRecurring(cursor.getInt(6) == 1);
                expense.setRecurringType(cursor.getString(7));
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }

    public Expense getExpense(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXPENSES, null, COL_EXPENSE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Expense expense = new Expense(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 1,
                    cursor.getString(7)
            );
            cursor.close();
            db.close();
            return expense;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }

    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, expense.getTitle());
        values.put(COL_DESCRIPTION, expense.getDescription());
        values.put(COL_AMOUNT, expense.getAmount());
        values.put(COL_CATEGORY, expense.getCategory());
        values.put(COL_DATE, expense.getDate());
        values.put(COL_IS_RECURRING, expense.isRecurring() ? 1 : 0);
        values.put(COL_RECURRING_TYPE, expense.getRecurringType());

        int result = db.update(TABLE_EXPENSES, values, COL_EXPENSE_ID + " = ?",
                new String[]{String.valueOf(expense.getId())});
        db.close();
        return result;
    }

    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSES, COL_EXPENSE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Category operations (existing methods remain unchanged)
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        String selectQuery = "SELECT c." + COL_CATEGORY_ID + ", c." + COL_CATEGORY_NAME +
                ", c." + COL_CATEGORY_COLOR + ", COALESCE(SUM(e." + COL_AMOUNT + "), 0) as total " +
                "FROM " + TABLE_CATEGORIES + " c LEFT JOIN " + TABLE_EXPENSES +
                " e ON c." + COL_CATEGORY_NAME + " = e." + COL_CATEGORY +
                " GROUP BY c." + COL_CATEGORY_ID + ", c." + COL_CATEGORY_NAME + ", c." + COL_CATEGORY_COLOR;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(0));
                category.setName(cursor.getString(1));
                category.setColor(cursor.getString(2));
                category.setTotalAmount(cursor.getDouble(3));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categoryList;
    }

    public long addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CATEGORY_NAME, category.getName());
        values.put(COL_CATEGORY_COLOR, category.getColor());

        long id = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return id;
    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, COL_CATEGORY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public double getTotalExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSES;
        Cursor cursor = db.rawQuery(query, null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getCategoryTotal(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_EXPENSES +
                " WHERE " + COL_CATEGORY + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{categoryName});

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }
}