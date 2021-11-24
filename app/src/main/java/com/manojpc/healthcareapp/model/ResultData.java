package com.manojpc.healthcareapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultData {
    @SerializedName("Appointee Users")
    @Expose
    private List<AppointeeUser> appointeeUsers = null;

    @SerializedName("User_details")
    @Expose
    private UserDetails userDetails;

    @SerializedName("Tasks")
    @Expose
    private List<Task> tasks = null;

    @SerializedName("Blogs")
    @Expose
    private List<Blog> blogs = null;

    //    get all users
    @SerializedName("All_Roles")
    @Expose
    private List<AllRole> allRoles = null;

    public List<AllRole> getAllRoles() {
        return allRoles;
    }

    public void setAllRoles(List<AllRole> allRoles) {
        this.allRoles = allRoles;
    }
//    get all users close


    public List<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public List<AppointeeUser> getAppointeeUsers() {
        return appointeeUsers;
    }

    public void setAppointeeUsers(List<AppointeeUser> appointeeUsers) {
        this.appointeeUsers = appointeeUsers;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
