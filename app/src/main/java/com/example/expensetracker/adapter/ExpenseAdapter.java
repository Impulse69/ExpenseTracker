package com.example.expensetracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.activity.EditExpenseActivity;
import com.example.expensetracker.model.Expense;
import java.util.List;
import java.text.DecimalFormat;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private Context context;
    private List<Expense> expenseList;
    private DecimalFormat decimalFormat;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
        this.decimalFormat = new DecimalFormat("#.##");
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.titleText.setText(expense.getTitle());
        holder.descriptionText.setText(expense.getDescription());
        holder.amountText.setText("$" + decimalFormat.format(expense.getAmount()));
        holder.categoryText.setText(expense.getCategory());
        holder.dateText.setText(expense.getDate());

        // Show recurring indicator
        if (expense.isRecurring()) {
            holder.recurringIcon.setVisibility(View.VISIBLE);
        } else {
            holder.recurringIcon.setVisibility(View.GONE);
        }

        // Set category color
        int color = getCategoryColor(expense.getCategory());
        holder.categoryIndicator.setBackgroundColor(color);

        // Click listener to edit expense
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditExpenseActivity.class);
            intent.putExtra("expense_id", expense.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void updateExpenses(List<Expense> newExpenses) {
        this.expenseList = newExpenses;
        notifyDataSetChanged();
    }

    private int getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "food": return Color.parseColor("#FF5722");
            case "transportation": return Color.parseColor("#2196F3");
            case "entertainment": return Color.parseColor("#FF9800");
            case "shopping": return Color.parseColor("#9C27B0");
            case "bills": return Color.parseColor("#4CAF50");
            case "healthcare": return Color.parseColor("#F44336");
            default: return Color.parseColor("#607D8B");
        }
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText, amountText, categoryText, dateText;
        ImageView recurringIcon;
        View categoryIndicator;
        CardView cardView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.expense_title);
            descriptionText = itemView.findViewById(R.id.expense_description);
            amountText = itemView.findViewById(R.id.expense_amount);
            categoryText = itemView.findViewById(R.id.expense_category);
            dateText = itemView.findViewById(R.id.expense_date);
            recurringIcon = itemView.findViewById(R.id.recurring_icon);
            categoryIndicator = itemView.findViewById(R.id.category_indicator);
            cardView = itemView.findViewById(R.id.expense_card);
        }
    }
}