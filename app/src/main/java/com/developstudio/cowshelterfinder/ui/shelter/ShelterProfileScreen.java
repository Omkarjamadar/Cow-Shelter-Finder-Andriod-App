package com.developstudio.cowshelterfinder.ui.shelter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.databinding.ActivityShelterProfileScreenBinding;
import com.developstudio.cowshelterfinder.modelClass.ShelterModelClass;
import com.developstudio.cowshelterfinder.repo.FirebaseHelper;
import com.developstudio.cowshelterfinder.utils.LocationUtils;
import com.developstudio.cowshelterfinder.utils.SharedPreferencesManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ShelterProfileScreen extends AppCompatActivity {
    private ActivityShelterProfileScreenBinding binding;
    SharedPreferencesManager manager;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityShelterProfileScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.screenLayout.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        manager = new SharedPreferencesManager(this);

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        userName = manager.getUsername("");

        if (!userName.isEmpty()) {

            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.getShelterById(userName, new FirebaseHelper.ShelterResponseData() {
                @Override
                public void onShelterDataRetrieved(ShelterModelClass modelClass) {

                    if (modelClass != null) {

                        setUpUi(modelClass);

                    }

                }

                @Override
                public void onError(String error) {
                    Toast.makeText(ShelterProfileScreen.this, error, Toast.LENGTH_SHORT).show();
                }
            });

        }

        binding.cardLogout.setOnClickListener(view ->
        {

            manager.saveUsername("");
            finish();
            finishAffinity();
        });


        binding.toolbarProfile.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        binding.updateLocation.setOnClickListener(view -> {

            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
            materialAlertDialogBuilder.setTitle("Update Location");
            materialAlertDialogBuilder.setMessage("Do you want to update Your location?");
            materialAlertDialogBuilder.setPositiveButton("Update", (dialogInterface, i) -> {
                updateLocation();
                dialogInterface.dismiss();
            }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            materialAlertDialogBuilder.create().show();

        });

    }

    private void updateLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getCurrentLocation();
            } else {
                showToast(getString(R.string.enable_location));
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        } else {
            requestPermissions();
        }
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // Method to get current location
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                updateLocationInFirebase(latitude, longitude);
            } else {
                showToast(getString(R.string.locationNotFound));
            }
        });
    }

    private void updateLocationInFirebase(double latitude, double longitude) {

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.updateShelterLocation(userName, latitude, longitude, new FirebaseHelper.ShelterUpdateLocationResponse() {
            @Override
            public void onUpdated() {
                showToast(getString(R.string.location_updated));
            }

            @Override
            public void onError(String error) {
                showToast(error);
            }
        });

    }


    private void setUpUi(ShelterModelClass modelClass) {
        Glide.with(this).load(modelClass.getShelterImage()).into(binding.shelterImage);
        binding.shelterName.setText(modelClass.getShelterName());
        binding.shelterOwnerName.setText(modelClass.getOwnerName());
        String address = LocationUtils.getAddressFromLatLng(this, modelClass.getLattitude(), modelClass.getLongitude());
        binding.shelterLocation.setText(address);
        binding.shelterEmail.setText(modelClass.getEmail());

        binding.screenLayout.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);

    }
}