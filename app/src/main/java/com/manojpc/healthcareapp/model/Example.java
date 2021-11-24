package com.manojpc.healthcareapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {

    @SerializedName("users")
    @Expose
    private Users users;
    @SerializedName("ResponseCode")
    @Expose
    private String responseCode;
    @SerializedName("Result")
    @Expose
    private String result;
    @SerializedName("ResponseMsg")
    @Expose
    private String responseMsg;

    public Users getUsers() {
        return users;
    }
    @SerializedName("ResultData")
    @Expose
    private ResultData resultData;

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public ResultData getResultData() {
        return resultData;
    }

    public void setResultData(ResultData resultData) {
        this.resultData = resultData;
    }
}
