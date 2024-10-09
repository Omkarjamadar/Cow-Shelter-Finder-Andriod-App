package com.developstudio.cowshelterfinder.modelClass;

public class UserModelClass {

    private String userName;
    private String name ;
    private String phoneNumber;
    private String password;
    private double latitude;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public UserModelClass() {
    }

    public UserModelClass(String userName, String name, String phoneNumber, String password, double latitude, double longitude) {
        this.userName = userName;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private Double longitude;


}
