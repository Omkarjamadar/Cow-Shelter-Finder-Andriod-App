package com.developstudio.cowshelterfinder.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
public class MapHelper {
    public static void openLocationOnMap(Context context, double latitude, double longitude) {
        // Create a Uri from the latitude and longitude with the query parameter
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + latitude + "," + longitude + "(Label)");

        // Create an Intent with the Uri
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Set the map zoom level (optional, may not be supported by all map apps)
        mapIntent.putExtra("zoom", 15);

        // Check if there's a map app available
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            // Start the map Intent
            context.startActivity(mapIntent);
        }
    }
}
