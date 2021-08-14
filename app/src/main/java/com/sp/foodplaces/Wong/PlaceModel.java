package com.sp.foodplaces.Wong;

public class PlaceModel {
    String name, rating, price, photo, address, placeID, fav;

    public PlaceModel(String name, String rating, String price, String photo, String address, String placeID, String fav){
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.photo = photo;
        this.address = address;
        this.placeID = placeID;
        this.fav = fav;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    public PlaceModel(){

    }
}
