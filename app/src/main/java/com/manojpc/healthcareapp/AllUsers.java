package com.manojpc.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.Utils.CustPrograssbar;
import com.manojpc.healthcareapp.adapter.AllUserAdapter;
import com.manojpc.healthcareapp.adapter.DoctorRequestAdapter;
import com.manojpc.healthcareapp.model.AllRole;
import com.manojpc.healthcareapp.model.Example;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class AllUsers extends AppCompatActivity implements GetResult.MyListener {

    RecyclerView rcv_docRequest;
    TextView tv_nothingIs;
    List<AllRole> doctorList;
    CustPrograssbar custPrograssbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        rcv_docRequest = findViewById(R.id.rcv_userRequest);
        tv_nothingIs = findViewById(R.id.tv_nothingIsU);

        custPrograssbar = new CustPrograssbar();
        custPrograssbar.progressCreate(AllUsers.this);
        custPrograssbar.setCancel(false);
        getDoctorsFromApi("doctor");

        getDoctorsFromApi("patient");
    }

    private void getDoctorsFromApi(String doctor) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("role", doctor);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getAllUsers((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.onNCHandle(call, "getAllUsers");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void callback(JsonObject result, String callNo) {

        if (callNo.equalsIgnoreCase("getAllUsers")) {

            Gson gson = new Gson();

            Example example = gson.fromJson(result.toString(),Example.class);

            doctorList = new ArrayList<>();

            if(example.getResultData().getAllRoles() == null)
            {
                tv_nothingIs.setVisibility(View.VISIBLE);
            }
            else
            {
                doctorList.addAll(example.getResultData().getAllRoles());

                AllUserAdapter adapter = new AllUserAdapter(AllUsers.this,doctorList);

                rcv_docRequest.setLayoutManager(new LinearLayoutManager(AllUsers.this));

                rcv_docRequest.setAdapter(adapter);
                custPrograssbar.close();
            }
        }

    }
}