package com.manojpc.healthcareapp.model;

public class User {
    private String name;
    private String adresse;
    private String tel;
    private String email;
    private String type;

    private String dob;
    private String age;

    public User(){
        //need firebase
    }

    public User(String name, String adresse, String tel, String email,String type,String age,String dob) {
        this.name = name;
        this.adresse = adresse;
        this.tel = tel;
        this.email = email;
        this.type = type;
        this.age = age;
        this.dob = dob;

    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
