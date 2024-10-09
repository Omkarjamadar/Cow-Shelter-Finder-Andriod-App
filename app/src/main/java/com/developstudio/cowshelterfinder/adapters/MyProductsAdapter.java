package com.developstudio.cowshelterfinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developstudio.cowshelterfinder.databinding.MyProductDemoBinding;
import com.developstudio.cowshelterfinder.modelClass.ProductModelClass;
import com.developstudio.cowshelterfinder.utils.Constants;

import java.util.List;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.ViewHolder> {

    private final List<ProductModelClass> list;
    private final Context context;

    public MyProductsAdapter(List<ProductModelClass> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyProductDemoBinding binding = MyProductDemoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.productDescription.setText(list.get(holder.getAdapterPosition()).getProductDescription());
        holder.binding.productName.setText(list.get(holder.getAdapterPosition()).getProductName());
        holder.binding.productPrice.setText(Constants.rupeeSign + list.get(holder.getAdapterPosition()).getProductPrice());
        Glide.with(context).load(list.get(holder.getAdapterPosition()).getProductImage()).into(holder.binding.productImage);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MyProductDemoBinding binding;

        public ViewHolder(@NonNull MyProductDemoBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
