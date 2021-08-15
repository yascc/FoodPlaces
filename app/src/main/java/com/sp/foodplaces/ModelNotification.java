package com.sp.foodplaces;

public class ModelNotification {
    String timestamp, notification, title;
    int image;

    public ModelNotification(
            int image,
            String timestamp,
            String notification,
            String title
            ) {

        this.timestamp = timestamp;
        this.image = image;
        this.notification = notification;
        this.title = title;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

}
