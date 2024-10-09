package com.developstudio.cowshelterfinder.modelClass;

public class ProductModelClass {

    private String productName;
    private String productImage;
    private String productDescription;
    private String shelterID;
    private int ProductPrice;
    private int productQuantity;
    private String productID;
    private Boolean inStock;

    public ProductModelClass() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getShelterID() {
        return shelterID;
    }

    public void setShelterID(String shelterID) {
        this.shelterID = shelterID;
    }

    public int getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(int productPrice) {
        this.ProductPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public ProductModelClass(String productName, String productImage, String productDescription,
                             String shelterID, int productPrice, int productQuantity, String productID, Boolean inStock) {
        this.productName = productName;
        this.productImage = productImage;
        this.productDescription = productDescription;
        this.shelterID = shelterID;
        this.ProductPrice = productPrice;
        this.productQuantity = productQuantity;
        this.productID = productID;
        this.inStock = inStock;
    }
}
