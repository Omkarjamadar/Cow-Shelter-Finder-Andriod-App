package com.developstudio.cowshelterfinder.modelClass;

public class ShelterModelClass {

    private String shelterName;
    private String ownerName;
    private Double longitude;
    private Double lattitude;
    private String email;
    private String phoneNumber;

    private String shelterID;
    private String shelterImage;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ShelterModelClass(String shelterName, String ownerName, Double longitude, Double lattitude, String email,
                             String phoneNumber, String shelterID, String shelterImage, String password) {
        this.shelterName = shelterName;
        this.ownerName = ownerName;
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.shelterID = shelterID;
        this.shelterImage = shelterImage;
        this.password = password;
    }

    public ShelterModelClass() {
    }

    public String getShelterName() {
        return shelterName;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getShelterID() {
        return shelterID;
    }

    public void setShelterID(String shelterID) {
        this.shelterID = shelterID;
    }

    public String getShelterImage() {
        return shelterImage;
    }

    public void setShelterImage(String shelterImage) {
        this.shelterImage = shelterImage;
    }


}
