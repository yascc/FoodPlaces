package com.sp.foodplaces;

public class ModelFavPlaceItem {
//coffeeItem
    private int imageResourse;
    private String title;
    private String key_id;
    private String address;
    private String rating;
    private String favStatus;

    public ModelFavPlaceItem() {
    }

    public ModelFavPlaceItem(int imageResourse, String title, String key_id, String address, String rating, String favStatus) {
        this.imageResourse = imageResourse;
        this.title = title;
        this.key_id = key_id;
        this.address = address;
        this.rating = rating;
        this.favStatus = favStatus;
    }

    public int getImageResourse() {
        return imageResourse;
    }

    public void setImageResourse(int imageResourse) {
        this.imageResourse = imageResourse;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(String favStatus) {
        this.favStatus = favStatus;
    }

}
