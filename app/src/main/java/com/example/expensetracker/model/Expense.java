package com.example.expensetracker.model;

public class Expense {
    private int id;
    private String title;
    private String description;
    private double amount;
    private String category;
    private String date;
    private boolean isRecurring;
    private String recurringType; // daily, weekly, monthly

    public Expense() {}

    public Expense(String title, String description, double amount, String category, String date, boolean isRecurring, String recurringType) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.isRecurring = isRecurring;
        this.recurringType = recurringType;
    }

    public Expense(int id, String title, String description, double amount, String category, String date, boolean isRecurring, String recurringType) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.isRecurring = isRecurring;
        this.recurringType = recurringType;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }

    public String getRecurringType() { return recurringType; }
    public void setRecurringType(String recurringType) { this.recurringType = recurringType; }
}