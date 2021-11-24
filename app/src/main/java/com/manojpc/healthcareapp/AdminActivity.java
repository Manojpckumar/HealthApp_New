package com.manojpc.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    Button signout,btn_docRequest,btn_allPatients,btn_manageFeedback,btn_adminBlogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        signout=findViewById(R.id.signOutBtnadmin);

        btn_docRequest=findViewById(R.id.btn_docRequest);
        btn_allPatients=findViewById(R.id.btn_allPatients);
        btn_manageFeedback=findViewById(R.id.btn_manageFeedback);
        btn_adminBlogs=findViewById(R.id.btn_adminBlog);

        signout.setOnClickListener(this);
        btn_docRequest.setOnClickListener(this);
        btn_allPatients.setOnClickListener(this);
        btn_manageFeedback.setOnClickListener(this);
        btn_adminBlogs.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.signOutBtnadmin:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btn_docRequest:
                Intent intent1 = new Intent(getApplicationContext(), DoctorRequest.class);
                startActivity(intent1);
                break;

            case R.id.btn_allPatients:
                Intent intent2 = new Intent(getApplicationContext(), AllUsers.class);
                startActivity(intent2);
                break;

            case R.id.btn_manageFeedback:
                Intent intent3 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent3);
                break;

            case R.id.btn_adminBlog:
                Intent intent4 = new Intent(getApplicationContext(), BlogActivity.class);
                startActivity(intent4);
                break;


        }

    }
}