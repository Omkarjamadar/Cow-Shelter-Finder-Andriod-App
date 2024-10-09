package com.developstudio.cowshelterfinder.ui.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.adapters.MyProductsAdapter;
import com.developstudio.cowshelterfinder.databinding.ActivityShelterMainScreenBinding;
import com.developstudio.cowshelterfinder.modelClass.ProductModelClass;
import com.developstudio.cowshelterfinder.modelClass.ShelterModelClass;
import com.developstudio.cowshelterfinder.repo.FirebaseHelper;
import com.developstudio.cowshelterfinder.utils.SharedPreferencesManager;

import java.util.List;

public class ShelterMainScreen extends AppCompatActivity
        implements FirebaseHelper.ShelterResponseData, FirebaseHelper.GetProductsResponse {
    private ActivityShelterMainScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityShelterMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SharedPreferencesManager sa = new SharedPreferencesManager(this);
        String username = sa.getUsername("");
        if (!username.isEmpty()) {
            showProgressBar();
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.getShelterById(username, this);
            firebaseHelper.getAllProductsByShelterID(username, this);
        }


        binding.fabCreateProduct.setOnClickListener(view -> {

            Intent intent = new Intent(this, CreateProductScreen.class);
            startActivity(intent);

        });

        binding.imageview.setOnClickListener(view -> startActivity(new Intent(this,ShelterProfileScreen.class)));

    }

    @Override
    public void onShelterDataRetrieved(ShelterModelClass modelClass) {

        if (modelClass != null) {

            setUpUI(modelClass);
        }

    }

    private void setUpUI(ShelterModelClass modelClass) {
        showScreen();
        binding.shelterName.setText(modelClass.getShelterName());
        if (!modelClass.getShelterImage().isEmpty()) {
            Glide.with(this).load(modelClass.getShelterImage()).into(binding.imageview);
        }
    }

    @Override
    public void onProductsRetrieved(List<ProductModelClass> productList) {
        if (!productList.isEmpty()) {
            setUpRecyclewView(productList);
        }

    }

    private void setUpRecyclewView(List<ProductModelClass> productList) {
        binding.len1.setVisibility(View.GONE);
        MyProductsAdapter adapter = new MyProductsAdapter(productList, this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        binding.myProductsRecycle.setLayoutManager(manager);
        binding.myProductsRecycle.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }


    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.cardOne.setVisibility(View.GONE);
        binding.len1.setVisibility(View.GONE);
        binding.fabCreateProduct.setVisibility(View.GONE);
    }

    private void showScreen() {
        binding.progressBar.setVisibility(View.GONE);
        binding.cardOne.setVisibility(View.VISIBLE);
        binding.fabCreateProduct.setVisibility(View.VISIBLE);
    }


    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        return super.getOnBackInvokedDispatcher();
    }
}