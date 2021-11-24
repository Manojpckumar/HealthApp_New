package com.manojpc.healthcareapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Task {

    @SerializedName("t_id")
    @Expose
    private String tId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("age_cat")
    @Expose
    private String ageCat;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("ap_date")
    @Expose
    private String apDate;
    @SerializedName("status")
    @Expose
    private String status;

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAgeCat() {
        return ageCat;
    }

    public void setAgeCat(String ageCat) {
        this.ageCat = ageCat;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getApDate() {
        return apDate;
    }

    public void setApDate(String apDate) {
        this.apDate = apDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}