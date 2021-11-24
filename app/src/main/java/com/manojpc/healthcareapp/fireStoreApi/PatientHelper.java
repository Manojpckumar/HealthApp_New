package com.manojpc.healthcareapp.fireStoreApi;

import com.manojpc.healthcareapp.model.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference PatientRef = db.collection("Patient");

    public static void addPatient(String name, String adresse, String tel, String dob, String age){
        Patient patient = new Patient(name,adresse,tel,FirebaseAuth.getInstance().getCurrentUser().getEmail(),"aaa", "aaa",dob,age);
        System.out.println("Create object patient");
        PatientRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(patient);
    }
}
