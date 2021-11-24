package com.manojpc.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.adapter.BlogAdapter;
import com.manojpc.healthcareapp.adapter.HealthTipAdapter;
import com.manojpc.healthcareapp.model.Example;
import com.manojpc.healthcareapp.model.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class HealthTipActivity extends AppCompatActivity implements GetResult.MyListener{

    RecyclerView rcv_healthtips;
    FloatingActionButton fab_addhealthtips;
    List health = new ArrayList();

    FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    String userRole,userAge,userId;

    List<Task> healthList;
    TextView tv_nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tip);

        rcv_healthtips = findViewById(R.id.rcv_healthtips);

        fab_addhealthtips = findViewById(R.id.fab_addhealthtips);
        tv_nothing = findViewById(R.id.tv_nothingTip);


        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        getStatusBYEmail(currentUser.getEmail());

        fab_addhealthtips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HealthTipActivity.this, AddHealthTip.class);
                startActivity(intent);

            }
        });

    }

    private void getStatusBYEmail(String email) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().statusbymail((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(HealthTipActivity.this);
            getResult.onNCHandle(call, "statusbymail");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void callback(JsonObject result, String callNo) {

        if (callNo.equalsIgnoreCase("statusbymail")) {

            Gson gson = new Gson();

            Example example = gson.fromJson(result.toString(), Example.class);

            if(example.getResultData().getUserDetails().getRole().equals("doctor"))
            {
                fab_addhealthtips.setVisibility(View.VISIBLE);

            }else if (example.getResultData().getUserDetails().getRole().equals("patient"))
            {
                fab_addhealthtips.setVisibility(View.GONE);
            }
            else
            {
                fab_addhealthtips.setVisibility(View.VISIBLE);
            }

            userRole = example.getResultData().getUserDetails().getRole();
            userAge = example.getResultData().getUserDetails().getAge();
            userId = example.getResultData().getUserDetails().getId();

            getHealthTips(userRole,userAge,userId);

        }

        else if (callNo.equalsIgnoreCase("getTodaystask")) {

            Gson gson = new Gson();

            Example example = gson.fromJson(result.toString(), Example.class);

            if(example.getResultData().getTasks() == null)
            {
                tv_nothing.setVisibility(View.VISIBLE);

            }else
            {
                healthList = new ArrayList<>();

                healthList.addAll(example.getResultData().getTasks());

                HealthTipAdapter adapter = new HealthTipAdapter(this,healthList,userRole);

                rcv_healthtips.setLayoutManager(new LinearLayoutManager(HealthTipActivity.this));

                rcv_healthtips.setAdapter(adapter);

            }




        }

    }

    private void getHealthTips(String userRole, String userAge, String userId) {

        JSONObject jsonObject = new JSONObject();
        try {
            if(userRole.equals("doctor"))
            {
                jsonObject.put("u_id", "0");
                jsonObject.put("doc_id", userId);
                jsonObject.put("age_limit", "0");

            }else if(userRole.equals("patient"))
            {
                jsonObject.put("u_id", userId);
                jsonObject.put("doc_id", "0");
                jsonObject.put("age_limit", userAge);

            }else
            {
                jsonObject.put("u_id", "0");
                jsonObject.put("doc_id", "0");
                jsonObject.put("age_limit", "0");
            }


            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getHealthTips((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(HealthTipActivity.this);
            getResult.onNCHandle(call, "getTodaystask");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}