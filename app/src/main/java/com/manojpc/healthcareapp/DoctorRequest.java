package com.manojpc.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.Utils.CustPrograssbar;
import com.manojpc.healthcareapp.adapter.DoctorRequestAdapter;
import com.manojpc.healthcareapp.model.AllRole;
import com.manojpc.healthcareapp.model.Example;
import com.manojpc.healthcareapp.model.ResponseBasic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class DoctorRequest extends AppCompatActivity implements  GetResult.MyListener ,RecyclerViewClickInterface {

    RecyclerView rcv_docRequest;
    List<AllRole> doctorList;
    TextView tv_nothingIs;
    CustPrograssbar custPrograssbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_request);

        rcv_docRequest = findViewById(R.id.rcv_docRequest);
        tv_nothingIs = findViewById(R.id.tv_nothingIs);

        custPrograssbar = new CustPrograssbar();
        custPrograssbar.progressCreate(DoctorRequest.this);
        custPrograssbar.setCancel(false);
        getDoctorsFromApi("doctor");

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

                DoctorRequestAdapter adapter = new DoctorRequestAdapter(this,doctorList,this);

                rcv_docRequest.setLayoutManager(new LinearLayoutManager(DoctorRequest.this));

                rcv_docRequest.setAdapter(adapter);
                custPrograssbar.close();
            }
        }

        else if(callNo.equalsIgnoreCase("DoctorApproval"))
        {
            if (callNo.equalsIgnoreCase("DoctorApproval")) {

                Gson gson = new Gson();

                ResponseBasic basic = gson.fromJson(result.toString(),ResponseBasic.class);

                if (basic.getResult().equals("true"))
                {
                    getDoctorsFromApi("Doctor");
                }
            }
        }

    }

    @Override
    public void onItemClick(int position, String chk) {

        if (chk.equalsIgnoreCase("APPROVE")) {
//            Toast.makeText(this, "EDIT" + doctorList.get(position).getId(), Toast.LENGTH_SHORT).show();
            updateDoctorStatus("1",doctorList.get(position).getId());
        }

    }

    private void updateDoctorStatus(String status, String id) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", status);
            jsonObject.put("d_id", id);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().doctorApproval((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.onNCHandle(call, "DoctorApproval");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}