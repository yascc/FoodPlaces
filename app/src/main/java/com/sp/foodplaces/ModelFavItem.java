package com.sp.foodplaces;

public class ModelFavItem {
    private String item_title;
    private String key_id;
    private String item_image;
    private String item_rating;
    private String item_address;

    public ModelFavItem() {
    }

    public ModelFavItem(String item_title, String key_id, String item_rating, String item_address, String item_image) {
        this.item_title = item_title;
        this.key_id = key_id;
        this.item_rating = item_rating;
        this.item_address = item_address;
        this.item_image = item_image;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getItem_rating() {
        return item_rating;
    }

    public void setItem_rating(String item_rating) {
        this.item_rating = item_rating;
    }

    public String getItem_address() {
        return item_address;
    }

    public void setItem_address(String item_address) {
        this.item_address = item_address;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }
}
