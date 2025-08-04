package com.example.expensetracker.model;

public class Category {
    private int id;
    private String name;
    private String color;
    private double totalAmount;

    public Category() {}

    public Category(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Category(int id, String name, String color, double totalAmount) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}