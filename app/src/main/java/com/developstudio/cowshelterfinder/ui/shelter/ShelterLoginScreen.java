package com.developstudio.cowshelterfinder.ui.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.databinding.ActivityShelterLoginScreeenBinding;
import com.developstudio.cowshelterfinder.repo.FirebaseHelper;
import com.developstudio.cowshelterfinder.utils.Constants;
import com.developstudio.cowshelterfinder.utils.SharedPreferencesManager;

public class ShelterLoginScreen extends AppCompatActivity {

    private ActivityShelterLoginScreeenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        // Inflate the layout using ViewBinding
        binding = ActivityShelterLoginScreeenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Apply window insets listener for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listener for sign up button
        binding.signUp.setOnClickListener(view -> startActivity(new Intent(ShelterLoginScreen.this, ShelterSingupScreen.class)));

        // Set click listener for login button
        binding.buttonLogin.setOnClickListener(view -> {
            // Check if email or password fields are empty
            if (TextUtils.isEmpty(binding.phoneNumber.getText())) {
                Toast.makeText(this, getString(R.string.enterNumber), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.password.getText())) {
                Toast.makeText(this, getString(R.string.enterPassword), Toast.LENGTH_SHORT).show();
            } else {
                // Perform login authentication
                performLogin(Constants.COUNTRY_CODE +binding.phoneNumber.getText().toString(), binding.password.getText().toString());
            }
        });
    }

    // Method to perform login authentication
    private void performLogin(String email, String password) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.searchShelterID(email, password, new FirebaseHelper.OnShelterIDResultListener() {
            @Override
            public void onShelterIDFound(String shelterID) {
                // Save username in SharedPreferences
                saveUsername(shelterID);
                // Start main activity after successful login
                Intent intent = new Intent(ShelterLoginScreen.this, ShelterMainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onPasswordIncorrect() {
                Toast.makeText(ShelterLoginScreen.this, getString(R.string.incrocctPassword), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserNotFound() {
                Toast.makeText(ShelterLoginScreen.this, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSearchFailed() {
                Toast.makeText(ShelterLoginScreen.this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Method to save username in SharedPreferences
    private void saveUsername(String shelterID) {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(ShelterLoginScreen.this);
        sharedPreferencesManager.saveUsername(shelterID);
    }
}