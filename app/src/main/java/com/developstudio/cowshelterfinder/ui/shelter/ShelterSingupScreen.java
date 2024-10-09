package com.developstudio.cowshelterfinder.ui.shelter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.databinding.ActivityShelterSingupScreenBinding;
import com.developstudio.cowshelterfinder.modelClass.ShelterModelClass;
import com.developstudio.cowshelterfinder.repo.FirebaseHelper;
import com.developstudio.cowshelterfinder.utils.Constants;
import com.developstudio.cowshelterfinder.utils.CreateShelterID;
import com.developstudio.cowshelterfinder.utils.SharedPreferencesManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class ShelterSingupScreen extends AppCompatActivity {

    private ActivityShelterSingupScreenBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private StorageReference storageRef;
    private static final String TAG = "ShelterSignupScreen";
    private Uri shelterImage = null;
    private FusedLocationProviderClient fusedLocationClient;

    // ActivityResultLauncher for permissions request
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    // ActivityResultLauncher for media selection
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        // Inflate layout using view binding
        binding = ActivityShelterSingupScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply window insets to adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set onClick listener for create account button
        binding.createAccount.setOnClickListener(view -> checkConditions());

        // Register ActivityResult handler for gallery permission request
        requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                results -> {
                    for (String permission : results.keySet()) {
                        boolean granted = Boolean.TRUE.equals(results.get(permission));
                        if (granted) {
                            pickFromGallery();
                        } else {
                           showToast(getString(R.string.permission_denied));
                        }
                    }
                });

        // Register ActivityResult handler for media selection
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                shelterImage = uri;
                binding.shelterImage.setImageURI(uri);
            } else {
               showToast(getString(R.string.noMediaSelected));
            }
        });

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Set onClick listener for shelter image view
        binding.shelterImage.setOnClickListener(view -> requestMediaPermission());

        binding.tittleBar.setNavigationOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }

    // Method to request permission for accessing media
    private void requestMediaPermission() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            };
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        requestPermissionsLauncher.launch(permissions);
    }

    // Method to pick image from gallery
    private void pickFromGallery() {
        pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    // Method to check input conditions before proceeding
    private void checkConditions() {
        if (isFieldEmpty(binding.inputEditTextShelterName)) {
            showToast("Enter Shelter name");
        } else if (isFieldEmpty(binding.editTextOwnerName)) {
            showToast("Enter Owner name");
        } else if (isFieldEmpty(binding.editTextEmailID)) {
            showToast("Enter Email ID");
        } else if (isFieldEmpty(binding.editTextPhoneNumber)) {
            showToast(getString(R.string.enterNumber));
        } else if (isFieldEmpty(binding.editPassword)) {
            showToast(getString(R.string.enterPassword));
        } else {
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
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // Method to get current location
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // startProgressbar
                onProgressOn();
                checkDataAndSaveData(latitude, longitude);
            } else {
                showToast(getString(R.string.locationNotFound));
            }
        });
    }

    // Method to upload image to Firebase Storage
    private void uploadImage(Uri fileUri, OnImageUploadListener listener) {
        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                String downloadUrl = downloadUri.toString();
                listener.onSuccess(downloadUrl);
            }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        }).addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Interface to handle image upload result
    interface OnImageUploadListener {
        void onSuccess(String downloadUrl);

        void onFailure(String errorMessage);
    }

    private void checkDataAndSaveData(double latitude, double longitude) {

        if (shelterImage != null) {
            uploadImage(shelterImage, new OnImageUploadListener() {
                @Override
                public void onSuccess(String downloadUrl) {
                    ShelterModelClass modelClass = new ShelterModelClass(Objects.requireNonNull(binding.inputEditTextShelterName.getText()).toString(),
                            Objects.requireNonNull(binding.editTextOwnerName.getText()).toString(),
                            longitude,
                            latitude,
                            Objects.requireNonNull(binding.editTextEmailID.getText()).toString(),
                           Constants.COUNTRY_CODE+ Objects.requireNonNull(binding.editTextPhoneNumber.getText()).toString(),
                            CreateShelterID.generateUniqueId(),
                            downloadUrl,
                            Objects.requireNonNull(binding.editPassword.getText()).toString());
                    saveDataInDatabase(modelClass);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(ShelterSingupScreen.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            ShelterModelClass modelClass = new ShelterModelClass(Objects.requireNonNull(binding.inputEditTextShelterName.getText()).toString(),
                    Objects.requireNonNull(binding.editTextOwnerName.getText()).toString(),
                    longitude,
                    latitude,
                    Objects.requireNonNull(binding.editTextEmailID.getText()).toString(),
                    Constants.COUNTRY_CODE + Objects.requireNonNull(binding.editTextPhoneNumber.getText()),
                    CreateShelterID.generateUniqueId(),
                    "", Objects.requireNonNull(binding.editPassword.getText()).toString());
            saveDataInDatabase(modelClass);
        }
    }

    private void saveDataInDatabase(ShelterModelClass modelClass) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.createShelter(modelClass, new FirebaseHelper.ShelterResponse() {
            @Override
            public void shelterCreated() {
                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(ShelterSingupScreen.this);
                sharedPreferencesManager.saveUsername(modelClass.getShelterID());
                Intent intent = new Intent(ShelterSingupScreen.this, ShelterMainScreen.class);
                showScreen();
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                showScreen();
                Toast.makeText(ShelterSingupScreen.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onProgressOn() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.li.setVisibility(View.GONE);
        binding.createAccount.setVisibility(View.GONE);
    }

    private void showScreen() {
        binding.progressBar.setVisibility(View.GONE);
        binding.li.setVisibility(View.VISIBLE);
        binding.createAccount.setVisibility(View.VISIBLE);

    }


}