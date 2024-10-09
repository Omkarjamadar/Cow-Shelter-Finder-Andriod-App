package com.developstudio.cowshelterfinder.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.databinding.ActivityProfileSelectionScreenBinding;
import com.developstudio.cowshelterfinder.ui.coustmer.CustomerLoginScreen;
import com.developstudio.cowshelterfinder.ui.shelter.ShelterLoginScreen;

public class ProfileSelectionScreen extends AppCompatActivity {

    private ActivityProfileSelectionScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityProfileSelectionScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        binding.cardCustomer.setOnClickListener(view ->
                {
                 Intent intent=new Intent(ProfileSelectionScreen.this, CustomerLoginScreen.class);
                 startActivity(intent);

                }

                );

        binding.cardShelter.setOnClickListener(view -> {
            Intent intent=new Intent(ProfileSelectionScreen.this, ShelterLoginScreen.class);
            startActivity(intent);
        });

    }


}