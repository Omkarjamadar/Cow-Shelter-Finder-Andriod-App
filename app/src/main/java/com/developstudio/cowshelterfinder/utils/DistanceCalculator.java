package com.developstudio.cowshelterfinder.utils;

import android.location.Location;

import java.text.DecimalFormat;

public class DistanceCalculator {
    public static float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        Location location1 = new Location("Point 1");
        location1.setLatitude(lat1);
        location1.setLongitude(lon1);
        Location location2 = new Location("Point 2");
        location2.setLatitude(lat2);
        location2.setLongitude(lon2);

        // Calculate distance in meters
        float distanceInMeters = location1.distanceTo(location2);

        // Convert distance to kilometers
        float distanceInKilometers = distanceInMeters / 1000.0f;

        // Format distance to two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedDistance = decimalFormat.format(distanceInKilometers);

        // Parse the formatted distance back to float
        float formattedDistanceFloat = Float.parseFloat(formattedDistance);

        return formattedDistanceFloat;
    }

}
