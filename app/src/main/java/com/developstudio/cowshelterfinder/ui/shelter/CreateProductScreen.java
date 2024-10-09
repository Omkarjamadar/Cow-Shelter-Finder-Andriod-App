package com.developstudio.cowshelterfinder.ui.shelter;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developstudio.cowshelterfinder.R;
import com.developstudio.cowshelterfinder.databinding.ActivityCreateProductScreenBinding;
import com.developstudio.cowshelterfinder.modelClass.ProductModelClass;
import com.developstudio.cowshelterfinder.repo.FirebaseHelper;
import com.developstudio.cowshelterfinder.utils.CreateProductID;
import com.developstudio.cowshelterfinder.utils.SharedPreferencesManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class CreateProductScreen extends AppCompatActivity {
    private ActivityCreateProductScreenBinding binding;
    private StorageReference storageRef;
    private Uri productImage;
    // ActivityResultLauncher for permissions request
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    // ActivityResultLauncher for media selection
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    private String myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateProductScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.addProduct.setOnClickListener(view -> {
            checkConditions();

        });

        SharedPreferencesManager sa = new SharedPreferencesManager(this);
        myUserId = sa.getUsername("");


        binding.productImage.setOnClickListener(view -> {
            requestMediaPermission();
        });


        binding.tittleBar.setNavigationOnClickListener(view ->
                getOnBackPressedDispatcher().onBackPressed());


        // Register ActivityResult handler for gallery permission request
        requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                results -> {
                    for (String permission : results.keySet()) {
                        boolean granted = Boolean.TRUE.equals(results.get(permission));
                        if (granted) {
                            pickFromGallery();
                        } else {
                            Toast.makeText(this, "Please grant permission to continue...", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, permission + " denied");
                        }
                    }
                });

        // Register ActivityResult handler for media selection
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                productImage = uri;
                binding.productImage.setImageURI(uri);
            } else {

                Toast.makeText(this, getString(R.string.noMediaSelected), Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


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


    // Method to upload image to Firebase Storage
    private void uploadImage(Uri fileUri, String ProductID, CreateProductScreen.OnImageUploadListener listener) {
        StorageReference imageRef = storageRef.child("images").child(myUserId).child(ProductID); //child("images/" + System.currentTimeMillis() + ".jpg");
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


    // Method to pick image from gallery
    private void pickFromGallery() {
        pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }


    // Method to check input conditions before proceeding
    private void checkConditions() {
        if (isFieldEmpty(binding.inputEditTextProductName)) {
            showToast(getString(R.string.enterProductName));
        } else if (isFieldEmpty(binding.editTextDescription)) {
            showToast(getString(R.string.enterDescription));
        } else if (isFieldEmpty(binding.editTextPrice)) {
            showToast(getString(R.string.enterPrice));
        } else if (isFieldEmpty(binding.editTextQuantity)) {
            showToast(getString(R.string.quantity));
        } else if (productImage == null) {
            showToast(getString(R.string.uploadImage));
        } else {
            showProgressBar();
            String productId = CreateProductID.generateUniqueId();

            uploadImage(productImage, productId, new OnImageUploadListener() {
                @Override
                public void onSuccess(String downloadUrl) {
                    ProductModelClass productModelClass = new ProductModelClass(Objects.requireNonNull(binding.inputEditTextProductName.getText()).toString(),
                            downloadUrl, Objects.requireNonNull(binding.editTextDescription.getText()).toString(),
                            myUserId,
                            Integer.parseInt(Objects.requireNonNull(binding.editTextPrice.getText()).toString()),
                            Integer.parseInt(Objects.requireNonNull(binding.editTextQuantity.getText()).toString())
                            , productId, true);
                    saveDataInDatabase(productModelClass);
                }

                @Override
                public void onFailure(String errorMessage) {
                    showScreen();
                    Toast.makeText(CreateProductScreen.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void saveDataInDatabase(ProductModelClass productModelClass) {

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.createProduct(productModelClass, new FirebaseHelper.CreateProductResponse() {
            @Override
            public void onProductCreated() {
                Toast.makeText(CreateProductScreen.this, getString(R.string.product_created), Toast.LENGTH_SHORT).show();
                showScreen();
                makeEveryThingNormal();
            }

            @Override
            public void onError(String error) {
                showScreen();
                Toast.makeText(CreateProductScreen.this, error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void makeEveryThingNormal() {
        Objects.requireNonNull(binding.editTextDescription.getText()).clear();

        Objects.requireNonNull(binding.inputEditTextProductName.getText()).clear();
        Objects.requireNonNull(binding.editTextPrice.getText()).clear();
        Objects.requireNonNull(binding.editTextQuantity.getText()).clear();
        productImage = null;

        binding.productImage.setImageDrawable(getDrawable(R.drawable.add_photo));

    }

    // Method to check if a text field is empty
    private boolean isFieldEmpty(TextInputEditText editText) {
        return editText.getText() == null || editText.getText().toString().trim().isEmpty();
    }

    // Method to show toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void showScreen() {
        binding.progressBar.setVisibility(View.GONE);
        binding.scrollView.setVisibility(View.VISIBLE);
        binding.addProduct.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(View.GONE);
        binding.addProduct.setVisibility(View.GONE);
    }

}