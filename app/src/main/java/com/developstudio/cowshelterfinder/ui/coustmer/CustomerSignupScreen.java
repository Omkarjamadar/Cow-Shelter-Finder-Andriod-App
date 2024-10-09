package com.developstudio.cowshelterfinder.ui.coustmer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developstudio.cowshelterfinder.MainActivity;
import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.databinding.ActivityCosutmerSignupScreenBinding;
import com.developstudio.cowshelterfinder.modelClass.UserModelClass;
import com.developstudio.cowshelterfinder.repo.FirebaseHelper;
import com.developstudio.cowshelterfinder.utils.Constants;
import com.developstudio.cowshelterfinder.utils.CreateUserID;
import com.developstudio.cowshelterfinder.utils.SharedPreferencesManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class CustomerSignupScreen extends AppCompatActivity {
    private ActivityCosutmerSignupScreenBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivityCosutmerSignupScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding.signupButton.setOnClickListener(view -> checkConditions());
    }

    private void checkConditions() {
        if (isFieldEmpty(binding.nameEditText)) {
            showToast(getString(R.string.enterName));
        } else if (isFieldEmpty(binding.phoneNumber)) {
            showToast(getString(R.string.enterNumber));
        } else if (isFieldEmpty(binding.password)) {
            showToast(getString(R.string.enterPassword));
        } else {
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    binding.signupButton.setText(getString(R.string.please_wait));
                    binding.signupButton.setFocusable(false);
                    getCurrentLocation();
                } else {
                    showToast(getString(R.string.enable_location));
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            } else {
                requestPermissions();
            }
        }
    }

    // Method to check if a text field is empty
    private boolean isFieldEmpty(TextInputEditText editText) {
        return editText.getText() == null || editText.getText().toString().trim().isEmpty();
    }

    // Method to show toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Method to check location permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Method to request location permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    getCurrentLocation();
                } else {
                    showToast(getString(R.string.enable_location));
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }

    // Method to check if location services are enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    // Method to get current location
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                checkDataAndSaveData(latitude, longitude);
            } else {
                binding.signupButton.setText(getString(R.string.signup));
                binding.signupButton.setFocusable(true);
                showToast(getString(R.string.locationNotFound));
            }
        });
    }

    private void checkDataAndSaveData(double latitude, double longitude) {

        String userID = CreateUserID.generateUniqueId();

        UserModelClass userModelClass = new UserModelClass(userID,
                Objects.requireNonNull(binding.nameEditText.getText()).toString(),
                Constants.COUNTRY_CODE + Objects.requireNonNull(binding.phoneNumber.getText()),
                Objects.requireNonNull(binding.password.getText()).toString(), latitude, longitude);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.createUserMethod(userModelClass, new FirebaseHelper.CreateUserResponse() {
            @Override
            public void onUserAdded() {
                binding.signupButton.setText(getString(R.string.done));
                binding.signupButton.setFocusable(false);
                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(
                        CustomerSignupScreen.this);
                sharedPreferencesManager.saveUsername(userID);
                startNewActivity();
            }

            @Override
            public void onError(String message) {
                binding.signupButton.setText(getString(R.string.signup));
                binding.signupButton.setFocusable(true);
                Toast.makeText(CustomerSignupScreen.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startNewActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}