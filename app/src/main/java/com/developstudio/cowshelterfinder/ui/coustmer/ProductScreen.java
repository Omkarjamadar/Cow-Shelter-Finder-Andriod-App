package com.developstudio.cowshelterfinder.ui.coustmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.adapters.MyProductsAdapter;
import com.developstudio.cowshelterfinder.databinding.ActivityProductScreenBinding;
import com.developstudio.cowshelterfinder.modelClass.ProductModelClass;
import com.developstudio.cowshelterfinder.repo.FirebaseHelper;

import java.util.List;

public class ProductScreen extends AppCompatActivity implements FirebaseHelper.GetProductsResponse {
    private ActivityProductScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProductScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Get the Intent that started this activity
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            String name = bundle.getString("name");
            String id = bundle.getString("id");

            binding.productToolbar.setTitle(name);

            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.getAllProductsByShelterID(id, this);

        }

        binding.productToolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    public void onProductsRetrieved(List<ProductModelClass> productList) {
        if (productList != null && !productList.isEmpty()) {
            binding.progressbar.setVisibility(View.GONE);
            MyProductsAdapter adapter = new MyProductsAdapter(productList, this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            binding.recycleProducts.setLayoutManager(layoutManager);
            binding.recycleProducts.setAdapter(adapter);

        } else {
            binding.progressbar.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.noProductFound), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onError(String error) {
        binding.progressbar.setVisibility(View.GONE);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();

    }
}