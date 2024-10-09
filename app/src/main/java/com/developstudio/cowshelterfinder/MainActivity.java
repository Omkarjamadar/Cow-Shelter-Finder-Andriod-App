package com.developstudio.cowshelterfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developstudio.cowshelterfinder.adapters.ShelterAdapter;
import com.developstudio.cowshelterfinder.databinding.ActivityMainBinding;
import com.developstudio.cowshelterfinder.modelClass.ShelterModelClass;
import com.developstudio.cowshelterfinder.modelClass.UserModelClass;
import com.developstudio.cowshelterfinder.repo.FirebaseHelper;
import com.developstudio.cowshelterfinder.ui.coustmer.AboutUsScreen;
import com.developstudio.cowshelterfinder.utils.Constants;
import com.developstudio.cowshelterfinder.utils.DistanceCalculator;
import com.developstudio.cowshelterfinder.utils.EmailSend;
import com.developstudio.cowshelterfinder.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebaseHelper.GetAllShelters, FirebaseHelper.UserResponseData {

    private ActivityMainBinding binding;
  private   FirebaseHelper firebaseHelper;
    private UserModelClass myUserModel;
    SharedPreferencesManager manager;
    private com.google.android.material.appbar.MaterialToolbar toolbar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         */

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycleViewMain);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        manager = new SharedPreferencesManager(this);
        String userName = manager.getUsername("");

        if (!userName.isEmpty()) {
            firebaseHelper = new FirebaseHelper();
            firebaseHelper.getUserDataById(userName, this);

        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setupTheNavigationDrawer();

    }


    private void setupTheNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            navigationDrawerHandler(item);
            return true;
        });
    }

    private void navigationDrawerHandler(MenuItem item) {

        if (item.getItemId() == R.id.myProfile) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.myLogout) {
            manager.saveUsername("");
            finish();
            finishAffinity();
        } else if (item.getItemId() == R.id.myHelp) {
            EmailSend.openEmailClient(Constants.supportEmail, Constants.subject, Constants.description, this);
        }else if (item.getItemId()==R.id.aboutUs){
            startActivity(new Intent(this, AboutUsScreen.class));
        }



    }


    @Override
    public void shelterList(List<ShelterModelClass> list) {
        ArrayList<ShelterModelClass> modelClasses = new ArrayList<>();

        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (DistanceCalculator.calculateDistance(myUserModel.getLatitude(), myUserModel.getLongitude(),
                        list.get(i).getLattitude(), list.get(i).getLongitude()) < 25) {
                    modelClasses.add(list.get(i));
                }

            }
        }
        if (!modelClasses.isEmpty()) {
            setUpUI(modelClasses);
        } else {
            Toast.makeText(this, getString(R.string.noShelterFound), Toast.LENGTH_SHORT).show();
        }

    }

    private void setUpUI(List<ShelterModelClass> list) {
        binding.progressBar.setVisibility(View.GONE);
        ShelterAdapter adapter = new ShelterAdapter(list, this, myUserModel);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onUserDataFound(UserModelClass modelClass) {
        if (modelClass != null) {
            myUserModel = modelClass;
            firebaseHelper.fetchSheltersFromFirestore(this);
            toolbar.setTitle(getString(R.string.hello) + modelClass.getName());
            setNameAndSubTitle(modelClass);
        }
    }


    private void setNameAndSubTitle(UserModelClass modelUser) {
        // Get the header view from the navigation view
        View headerView = binding.navigationView.getHeaderView(0);
        // Check if the activity is not destroyed
        if (!isDestroyed()) {
            // Find the TextView and ImageView in the header view
            TextView navUsername = headerView.findViewById(R.id.sidebarUserName);
            // Set the username in the TextView
            navUsername.setText(modelUser.getName());
            // Find the subtitle TextView and set its text
            TextView subtitleTextview = headerView.findViewById(R.id.subTitle);
            subtitleTextview.setText(getString(R.string.yourID) + modelUser.getUserName());

        }
    }

    @Override
    public void onError(String message) {
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}