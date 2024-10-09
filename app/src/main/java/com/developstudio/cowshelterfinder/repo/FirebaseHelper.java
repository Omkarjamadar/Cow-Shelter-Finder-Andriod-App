package com.developstudio.cowshelterfinder.repo;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.developstudio.cowshelterfinder.modelClass.ProductModelClass;
import com.developstudio.cowshelterfinder.modelClass.ShelterModelClass;
import com.developstudio.cowshelterfinder.modelClass.UserModelClass;
import com.developstudio.cowshelterfinder.utils.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseHelper {
    FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    private ListenerRegistration listenerRegistration;

    public void createShelter(ShelterModelClass modelClass, ShelterResponse response) {
        // Add a new document with a generated ID
        db.collection(Constants.SHELTERS)
                .document(modelClass.getShelterID()) // Use the document method to specify the ID
                .set(modelClass) // Use set() instead of add() to create the document with the specified ID
                .addOnSuccessListener(unused -> response.shelterCreated())
                .addOnFailureListener(e -> {
                    response.onError(e.getMessage());
                });
    }


    public interface ShelterResponse {
        void shelterCreated();

        void onError(String error);
    }


    public void createUserMethod(UserModelClass modelClass, CreateUserResponse response) {
        db.collection(Constants.CUSTOMERS).document(modelClass.getUserName()).set(modelClass)
                .addOnSuccessListener(unused -> response.onUserAdded()).addOnFailureListener(e -> response.onError(e.getMessage()));

    }

    public interface CreateUserResponse {
        void onUserAdded();

        void onError(String message);

    }

    // Define the CreateProductResponse interface
    public interface CreateProductResponse {
        void onProductCreated();

        void onError(String error);
    }


    // Method to create a product
    public void createProduct(ProductModelClass productModelClass, CreateProductResponse response) {
        // Get the reference to the document with shelterID
        DocumentReference shelterDocRef = db.collection(Constants.PRODUCTS).document(productModelClass.getShelterID());

        // Get the reference to the subcollection with productID
        DocumentReference productDocRef = shelterDocRef.collection(Constants.PRODUCTS).document(productModelClass.getProductID());

        // Set the product data
        productDocRef.set(productModelClass)
                .addOnSuccessListener(unused -> response.onProductCreated())
                .addOnFailureListener(e -> response.onError(e.getMessage()));
    }

    public void searchShelterID(String number, String password, OnShelterIDResultListener listener) {
        Query query = db.collection(Constants.SHELTERS).whereEqualTo("phoneNumber", number);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean emailFound = false;
                for (DocumentSnapshot document : task.getResult()) {
                    String shelterID = document.getString("shelterID");
                    String storedPassword = document.getString("password");
                    emailFound = true;
                    if (storedPassword != null && storedPassword.equals(password)) {
                        listener.onShelterIDFound(shelterID);
                        return; // Stop further iteration
                    }
                }
                if (!emailFound) {
                    listener.onUserNotFound();
                } else {
                    listener.onPasswordIncorrect();
                }
            } else {
                listener.onSearchFailed();
            }
        });
    }

    public interface OnShelterIDResultListener {
        void onShelterIDFound(String shelterID);

        void onPasswordIncorrect();

        void onUserNotFound();

        void onSearchFailed();
    }


    // Define the ShelterResponse interface
    public interface ShelterResponseData {
        void onShelterDataRetrieved(ShelterModelClass modelClass);

        void onError(String error);
    }


    // Single method to create or get shelter data by shelter ID
    // Method to get shelter data by shelter ID
    public void getShelterById(String shelterID, ShelterResponseData response) {
        DocumentReference docRef = db.collection(Constants.SHELTERS).document(shelterID);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ShelterModelClass shelter = documentSnapshot.toObject(ShelterModelClass.class);
                        response.onShelterDataRetrieved(shelter);
                    } else {
                        response.onError("Shelter not found");
                    }
                })
                .addOnFailureListener(e -> response.onError(e.getMessage()));
    }


    // Define the GetProductsResponse interface
    public interface GetProductsResponse {
        void onProductsRetrieved(List<ProductModelClass> productList);

        void onError(String error);
    }


    // Method to get real-time updates of all products by shelter ID
    public void getAllProductsByShelterID(String shelterID, GetProductsResponse response) {
        CollectionReference productsRef = db.collection(Constants.PRODUCTS).document(shelterID).collection(Constants.PRODUCTS);

        listenerRegistration = productsRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                response.onError(e.getMessage());
                return;
            }

            if (queryDocumentSnapshots != null) {
                List<ProductModelClass> productList = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    ProductModelClass product = document.toObject(ProductModelClass.class);
                    productList.add(product);
                }
                response.onProductsRetrieved(productList);
            } else {
                response.onError("No products found");
            }
        });
    }

    // Method to remove the listener when no longer needed
    public void removeListener() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }


    public void searchUserID(String number, String password, OnUserIDResultListener listener) {
        Query query = db.collection(Constants.CUSTOMERS).whereEqualTo("phoneNumber", number);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean emailFound = false;
                for (DocumentSnapshot document : task.getResult()) {
                    String shelterID = document.getString("userName");
                    String storedPassword = document.getString("password");
                    emailFound = true;
                    if (storedPassword != null && storedPassword.equals(password)) {
                        listener.onUserFound(shelterID);
                        return; // Stop further iteration
                    }
                }
                if (!emailFound) {
                    listener.onUserNotFound();
                } else {
                    listener.onPasswordIncorrect();
                }
            } else {
                listener.onSearchFailed();
            }
        });
    }

    public interface OnUserIDResultListener {
        void onUserFound(String userID);

        void onPasswordIncorrect();

        void onUserNotFound();

        void onSearchFailed();
    }


    public void fetchSheltersFromFirestore(GetAllShelters response) {
        List<ShelterModelClass> shelters = new ArrayList<ShelterModelClass>();

        db.collection(Constants.SHELTERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        shelters.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                ShelterModelClass shelter = document.toObject(ShelterModelClass.class);
                                shelters.add(shelter);
                            }
                            response.shelterList(shelters);

                        }
                    } else {
                        response.onError("Error getting documents. " + Objects.requireNonNull(task.getException()).getMessage());

                    }
                });


    }


    public interface GetAllShelters {

        void shelterList(List<ShelterModelClass> list);

        void onError(String message);

    }


    // Method to get User data by user ID
    public void getUserDataById(String userID, UserResponseData response) {
        DocumentReference docRef = db.collection(Constants.CUSTOMERS).document(userID);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModelClass data = documentSnapshot.toObject(UserModelClass.class);
                        response.onUserDataFound(data);
                    } else {
                        response.onError("User not found");
                    }
                })
                .addOnFailureListener(e -> response.onError(e.getMessage()));
    }


    public interface UserResponseData {
        void onUserDataFound(UserModelClass modelClass);

        void onError(String error);
    }


    public void updateShelterLocation(String shelterID, double newLatitude, double newLongitude, ShelterUpdateLocationResponse response) {
        DocumentReference shelterRef = db.collection(Constants.SHELTERS).document(shelterID);

        shelterRef.update("latitude", newLatitude, "longitude", newLongitude)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        response.onUpdated();
                    } else {
                        response.onError(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });


    }


    public interface ShelterUpdateLocationResponse {
        void onUpdated();

        void onError(String error);
    }


}
