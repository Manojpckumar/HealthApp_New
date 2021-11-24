package com.manojpc.healthcareapp.fireStoreApi;

import com.manojpc.healthcareapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference UsersRef = db.collection("User");

    public static void addUser(String name, String adresse, String tel,String type,String dob,String age){

        User user = new User(name,adresse,tel,FirebaseAuth.getInstance().getCurrentUser().getEmail(),type,age,dob);
        UsersRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(user);

    }
}
