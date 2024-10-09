package com.developstudio.cowshelterfinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.databinding.DemoShelterLayoutBinding;
import com.developstudio.cowshelterfinder.modelClass.ShelterModelClass;
import com.developstudio.cowshelterfinder.modelClass.UserModelClass;
import com.developstudio.cowshelterfinder.ui.coustmer.ProductScreen;
import com.developstudio.cowshelterfinder.utils.DialerHelper;
import com.developstudio.cowshelterfinder.utils.DistanceCalculator;
import com.developstudio.cowshelterfinder.utils.LocationUtils;
import com.developstudio.cowshelterfinder.utils.MapHelper;

import java.util.List;

public class ShelterAdapter extends RecyclerView.Adapter<ShelterAdapter.ViewModel> {

    private final List<ShelterModelClass> list;
    private final Context context;
    private final UserModelClass userModelClass;

    public ShelterAdapter(List<ShelterModelClass> list, Context context, UserModelClass userModelClass) {
        this.list = list;
        this.userModelClass = userModelClass;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DemoShelterLayoutBinding binding = DemoShelterLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewModel(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewModel holder, int position) {

        if (list.get(holder.getAdapterPosition()).getShelterImage() != null) {
            Glide.with(context).load(list.get(holder.getAdapterPosition()).getShelterImage()).into(holder.binding.shelterImage);
        } else {
            try {
                Glide.with(context)
                        .load(R.drawable.shelter)
                        .into(holder.binding.shelterImage);
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        holder.binding.shelterName.setText(list.get(holder.getAdapterPosition()).getShelterName());
        holder.binding.shelterAddress.setText(LocationUtils.getAddressFromLatLng(context, list.get(holder.getAdapterPosition()).getLattitude(), list.get(holder.getAdapterPosition()).getLongitude()));

        holder.binding.buttonLocation.setText(DistanceCalculator.calculateDistance(userModelClass.getLatitude(), userModelClass.getLongitude(),
                list.get(holder.getAdapterPosition()).getLattitude(), list.get(holder.getAdapterPosition()).getLongitude()) + " km.");


        holder.binding.buttonLocation.setOnClickListener(view -> {

            MapHelper.openLocationOnMap(context, list.get(holder.getAdapterPosition()).getLattitude(), list.get(holder.getAdapterPosition()).getLongitude());

        });

        holder.binding.callButton.setOnClickListener(view -> {
            DialerHelper.openDialer(context, list.get(holder.getAdapterPosition()).getPhoneNumber());
        });

        holder.binding.cardDemo.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductScreen.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", list.get(holder.getAdapterPosition()).getShelterName());
            bundle.putString("id", list.get(holder.getAdapterPosition()).getShelterID());
            intent.putExtra("bundle", bundle);

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewModel extends RecyclerView.ViewHolder {
        DemoShelterLayoutBinding binding;

        public ViewModel(@NonNull DemoShelterLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
