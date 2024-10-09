package com.developstudio.cowshelterfinder.ui.coustmer;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developstudio.cowshelterfinder.R;

public class AboutUsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_us_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        WebView webView = findViewById(R.id.webView);

        // Enable JavaScript if needed
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // HTML content
        String htmlContent = "<html><body>" +
                "<h1>Welcome to Cow Shelter Finder</h1>" +
                "<p>Where compassion meets technology to make a difference in the lives of our bovine friends.</p>" +
                "<h2>Our Mission:</h2>" +
                "<p>At Cow Shelter Finder, our mission is to connect cows in need with caring shelters and compassionate individuals. " +
                "We believe that every cow deserves love, care, and protection, and our platform strives to make this a reality by providing a seamless way for users to locate and support cow shelters around the globe.</p>" +
                "<h2>Key Features:</h2>" +
                "<ul>" +
                "<li>Search and discover cow shelters in your area or around the world.</li>" +
                "<li>Learn about each shelter's mission, facilities, and the cows they care for.</li>" +
                "<li>Support shelters through donations, volunteering, and other means of assistance.</li>" +
                "</ul>" +
                "<p>Thank you for joining us on this journey to create a brighter future for cows everywhere.</p>" +
                "</body></html>";

        // Load the HTML content
        webView.loadData(htmlContent, "text/html", "UTF-8");




    }
}