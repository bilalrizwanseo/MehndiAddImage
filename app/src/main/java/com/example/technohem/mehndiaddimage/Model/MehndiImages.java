package com.example.technohem.mehndiaddimage.Model;

public class MehndiImages {

    private String pid, category, image, date, time;

    public MehndiImages() {
    }

    public MehndiImages(String pid, String category, String image, String date, String time) {
        this.pid = pid;
        this.category = category;
        this.image = image;
        this.date = date;
        this.time = time;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
