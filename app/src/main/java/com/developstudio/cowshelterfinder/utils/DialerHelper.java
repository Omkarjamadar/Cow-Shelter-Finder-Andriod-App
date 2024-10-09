package com.developstudio.cowshelterfinder.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class DialerHelper {
    public static void openDialer(Context context, String phoneNumber) {
        // Create a Uri from the phone number
        Uri number = Uri.parse("tel:" + phoneNumber);

        // Create an Intent with the action ACTION_DIAL and the phone number Uri
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, number);

        // Check if there's a dialer app available
        if (dialIntent.resolveActivity(context.getPackageManager()) != null) {
            // Start the dialer Intent
            context.startActivity(dialIntent);
        }
    }
}