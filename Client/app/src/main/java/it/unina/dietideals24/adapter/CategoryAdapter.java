package it.unina.dietideals24.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.unina.dietideals24.R;
import it.unina.dietideals24.adapter.entity.CategoryItem;
import it.unina.dietideals24.view.activity.AuctionsByCategoryActivity;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    ArrayList<CategoryItem> categories;
    Context context;
    ShapeEnum shape;
    int layout;

    /**
     * Constructor of CategoryAdapter
     *
     * @param categories list of categories to put into the Adapter
     * @param shape      shape of the Adapter to use, use public (static) enum ShapeEnum with constants ROUND or SQUARE
     */
    public CategoryAdapter(ArrayList<CategoryItem> categories, ShapeEnum shape) {
        this.categories = categories;
        this.shape = shape;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        if (shape.equals(ShapeEnum.ROUND))
            layout = R.layout.category_round_item;
        else if (shape.equals(ShapeEnum.SQUARE))
            layout = R.layout.category_square_item;

        View inflate = LayoutInflater.from(context).inflate(layout, parent, false);
        return new CategoryViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.nameCategory.setText(categories.get(holder.getAdapterPosition()).getName());
        holder.imageCategory.setImageResource(categories.get(holder.getAdapterPosition()).getImage());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), AuctionsByCategoryActivity.class);
            intent.putExtra("category", categories.get(holder.getAdapterPosition()).getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public enum ShapeEnum {ROUND, SQUARE}

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameCategory;
        ImageView imageCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            nameCategory = itemView.findViewById(R.id.nameCategory);
            imageCategory = itemView.findViewById(R.id.imageCategory);
        }
    }
}
