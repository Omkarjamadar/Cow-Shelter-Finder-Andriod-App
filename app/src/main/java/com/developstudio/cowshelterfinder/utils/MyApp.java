package com.developstudio.cowshelterfinder.utils;

import android.app.Application;

import com.google.android.material.color.DynamicColors;
import com.google.firebase.FirebaseApp;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
