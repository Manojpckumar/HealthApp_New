package com.manojpc.healthcareapp.fireStoreApi;

import com.manojpc.healthcareapp.model.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference DoctorRef = db.collection("Doctor");

    public static void addDoctor(String name, String adresse, String tel,String specialite,String dob,String age,String d_id){
        Doctor doctor = new Doctor(name,adresse,tel, FirebaseAuth.getInstance().getCurrentUser().getEmail(),specialite,dob,age,d_id);

        DoctorRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(doctor);

    }
}
