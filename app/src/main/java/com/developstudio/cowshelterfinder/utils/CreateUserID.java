package com.developstudio.cowshelterfinder.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CreateUserID {

    private static final String PREFIX = "User";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");

    public static void main(String[] args) {
        System.out.println(generateUniqueId());
    }
    public static String generateUniqueId() {
        // Generating a timestamp
        String timestamp = dateFormat.format(new Date());

        // Generating random digits to complete the unique ID
        String randomDigits = generateRandomDigits(12 - timestamp.length());

        // Combining prefix, timestamp, and random digits to create a unique ID
        return PREFIX + timestamp + randomDigits;
    }

    // Method to generate random digits of a specified length
    private static String generateRandomDigits(int length) {
        Random random = new Random(System.currentTimeMillis());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10)); // Generate a random digit between 0 and 9
        }
        return stringBuilder.toString();
    }
}
