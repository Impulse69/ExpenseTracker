package com.example.expensetracker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.model.Category;
import java.util.List;
import java.text.DecimalFormat;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private DecimalFormat decimalFormat;
    private OnCategoryDeleteListener deleteListener;

    public interface OnCategoryDeleteListener {
        void onDeleteCategory(int categoryId, String categoryName);
    }

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.decimalFormat = new DecimalFormat("#.##");
    }

    public void setOnCategoryDeleteListener(OnCategoryDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.categoryName.setText(category.getName());
        holder.categoryTotal.setText("$" + decimalFormat.format(category.getTotalAmount()));

        // Set category color
        try {
            int color = Color.parseColor(category.getColor());
            holder.categoryIndicator.setBackgroundColor(color);
        } catch (Exception e) {
            holder.categoryIndicator.setBackgroundColor(Color.parseColor("#607D8B"));
        }

        // Delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteCategory(category.getId(), category.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void updateCategories(List<Category> newCategories) {
        this.categoryList = newCategories;
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, categoryTotal;
        View categoryIndicator;
        ImageView deleteButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryTotal = itemView.findViewById(R.id.category_total);
            categoryIndicator = itemView.findViewById(R.id.category_color_indicator);
            deleteButton = itemView.findViewById(R.id.delete_category_btn);
        }
    }
}