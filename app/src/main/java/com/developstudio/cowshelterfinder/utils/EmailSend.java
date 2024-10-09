package com.developstudio.cowshelterfinder.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class EmailSend {

    public static void openEmailClient(String email, String subject, String description, Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, description);

        // Verify that there is an email client available to handle the intent
        //if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
        context.startActivity(emailIntent);
        //  } else {
        // Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        // Handle the case where no email clients are installed
        // You can show a message to the user or take appropriate action
        //  }
    }
}
