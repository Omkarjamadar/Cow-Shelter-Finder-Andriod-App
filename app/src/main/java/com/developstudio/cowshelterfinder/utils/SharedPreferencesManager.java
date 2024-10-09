package com.developstudio.cowshelterfinder.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private Context context;
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }
    public void saveUsername(String username) {
        sharedPreferences.edit().putString("username", username).apply();
    }
    public String getUsername(String defaultValue) {
        return sharedPreferences.getString("username", defaultValue);
    }
}
