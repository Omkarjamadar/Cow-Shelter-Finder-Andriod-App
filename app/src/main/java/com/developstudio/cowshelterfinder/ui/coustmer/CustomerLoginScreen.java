package com.developstudio.cowshelterfinder.ui.coustmer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developstudio.cowshelterfinder.MainActivity;
import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.databinding.ActivityCustomerLoginScreenBinding;
import com.developstudio.cowshelterfinder.repo.FirebaseHelper;
import com.developstudio.cowshelterfinder.utils.Constants;
import com.developstudio.cowshelterfinder.utils.SharedPreferencesManager;

public class CustomerLoginScreen extends AppCompatActivity {
    private ActivityCustomerLoginScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCustomerLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        binding.signUpText.setOnClickListener(view -> {

            Intent intent = new Intent(CustomerLoginScreen.this, CustomerSignupScreen.class);
            startActivity(intent);
        });


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

    private void performLogin(String string, String string1) {

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.searchUserID(string, string1, new FirebaseHelper.OnUserIDResultListener() {
            @Override
            public void onUserFound(String userID) {
                // Save username in SharedPreferences
                saveUsername(userID);
                // Start main activity after successful login

                Intent intent = new Intent(CustomerLoginScreen.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onPasswordIncorrect() {
                Toast.makeText(CustomerLoginScreen.this, getString(R.string.incrocctPassword), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserNotFound() {
                Toast.makeText(CustomerLoginScreen.this, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSearchFailed() {
                Toast.makeText(CustomerLoginScreen.this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to save username in SharedPreferences
    private void saveUsername(String userID) {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(CustomerLoginScreen.this);
        sharedPreferencesManager.saveUsername(userID);
    }
}