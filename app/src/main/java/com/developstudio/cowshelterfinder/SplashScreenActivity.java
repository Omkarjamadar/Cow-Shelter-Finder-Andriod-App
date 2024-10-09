package com.developstudio.cowshelterfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developstudio.cowshelterfinder.databinding.ActivitySplashScreenBinding;
import com.developstudio.cowshelterfinder.ui.ProfileSelectionScreen;
import com.developstudio.cowshelterfinder.ui.shelter.ShelterMainScreen;
import com.developstudio.cowshelterfinder.utils.SharedPreferencesManager;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        com.developstudio.cowshelterfinder.databinding.ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);

        String id = sharedPreferencesManager.getUsername("");

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (id.isEmpty()) {
                startActivity(new Intent(SplashScreenActivity.this, ProfileSelectionScreen.class));
                finish();
            } else {

                if (id.startsWith("Shelter")) {
                    startActivity(new Intent(SplashScreenActivity.this, ShelterMainScreen.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }

            }
        }, 1500);


    }
}