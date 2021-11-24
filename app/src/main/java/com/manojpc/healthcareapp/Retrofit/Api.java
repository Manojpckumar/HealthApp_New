package com.manojpc.healthcareapp.Retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {


    @POST(APIClient.APPEND_URL + "register")
    Call<JsonObject> registerphase1(@Body JsonObject object);


    @POST(APIClient.APPEND_URL + "update_user")
    Call<JsonObject> registerphase2(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "login")
    Call<JsonObject> login(@Body JsonObject object);

    @POST(APIClient.APPEND_URL + "getStatusbyemail")
    Call<JsonObject> statusbymail(@Body JsonObject object);

//    newly added

    //    add booking
    @POST(APIClient.APPEND_URL + "addAppointment")
    Call<JsonObject> addBookingToApi(@Body JsonObject object);

    //    get Patients by doctor id
    @POST(APIClient.APPEND_URL + "getAppointeeUsers")
    Call<JsonObject> getPatientsById(@Body JsonObject object);

    //    add health tip to api
    @POST(APIClient.APPEND_URL + "addHealthtips")
    Call<JsonObject> createHealthTip(@Body JsonObject object);

    //    get health tip from api
    @POST(APIClient.APPEND_URL + "getTodaystask")
    Call<JsonObject> getHealthTips(@Body JsonObject object);

    //    get blog tip to api
    @POST(APIClient.APPEND_URL + "addBlogs")
    Call<JsonObject> createBlog(@Body JsonObject object);

    // get all blogs from API
    @POST(APIClient.APPEND_URL + "getAllBlogs")
    Call<JsonObject> getAllBlogs(@Body JsonObject object);

    // delete blog from api
    @POST(APIClient.APPEND_URL + "del_wrt_blogs")
    Call<JsonObject> deleteBlog(@Body JsonObject object);

    // update blog status start completed
    @POST(APIClient.APPEND_URL + "updateTipsstatus")
    Call<JsonObject> updateStatus(@Body JsonObject object);

    // get all users from api
    @POST(APIClient.APPEND_URL + "getAllUsers")
    Call<JsonObject> getAllUsers(@Body JsonObject object);

    // doctorApproval
    @POST(APIClient.APPEND_URL + "DoctorApproval")
    Call<JsonObject> doctorApproval(@Body JsonObject object);




}
