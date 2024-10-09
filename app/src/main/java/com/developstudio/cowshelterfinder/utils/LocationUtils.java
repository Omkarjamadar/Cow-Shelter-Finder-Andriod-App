package com.developstudio.cowshelterfinder.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {
    private static final String TAG = "GeocodeHelper";

    public static String getAddressFromLatLng(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = "Address not found";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addressObj = addresses.get(0);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i <= addressObj.getMaxAddressLineIndex(); i++) {
                    sb.append(addressObj.getAddressLine(i)).append("\n");
                }

                address = sb.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to get address from latitude and longitude", e);
        }

        return address;
    }
}
