package com.sp.foodplaces;

public class ModelNotification {
    String timestamp,
            //pID, pUid, sUid,
            notification, title;
    int image;

    public ModelNotification(
            //String pID,
            int image,
            String timestamp,
            //String pUid,
            //String sUid,
            String notification,
            String title
            ) {
        //this.pID = pID;
        this.timestamp = timestamp;
        //this.pUid = pUid;
        //this.sUid = sUid;
        this.image = image;
        this.notification = notification;
        this.title = title;
    }

/*    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }*/

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

/*


    public String getpUid() {
        return pUid;
    }

    public void setpUid(String pUid) {
        this.pUid = pUid;
    }

    public String getsUid() {
        return sUid;
    }

    public void setsUid(String sUid) {
        this.sUid = sUid;
    }*/
}
