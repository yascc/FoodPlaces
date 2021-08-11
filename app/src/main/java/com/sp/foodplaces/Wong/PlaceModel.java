package com.sp.foodplaces.Wong;

public class PlaceModel {
    String name, rating, price, photo;

    public PlaceModel(String name,String rating,String price, String photo){
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() { return rating; }

    public void setRating(String rating) { this.rating = rating; }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public PlaceModel(){

    }
}
