package com.manojpc.healthcareapp.model;

public class Doctor {
    private String name;
    private String adresse;
    private String tel;
    private String email;
    private String specialite;

    private String dob;
    private String age;
    private String d_id;


    public Doctor(){
        //needed for firebase
    }

    public Doctor(String name, String adresse, String tel, String email, String specialite,String dob,String age,String d_id) {
        this.name = name;
        this.adresse = adresse;
        this.tel = tel;
        this.email = email;
        this.specialite = specialite;

        this.dob = dob;
        this.age = age;
        this.d_id = d_id;
    }

    public String getD_id() {
        return d_id;
    }

    public void setD_id(String d_id) {
        this.d_id = d_id;
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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}
