package com.manojpc.healthcareapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.Utils.CustPrograssbar;
import com.manojpc.healthcareapp.fireStoreApi.DoctorHelper;
import com.manojpc.healthcareapp.fireStoreApi.PatientHelper;
import com.manojpc.healthcareapp.fireStoreApi.UserHelper;
import com.manojpc.healthcareapp.model.ResponseBasic;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;

import static android.widget.AdapterView.*;

public class FirstSigninActivity extends AppCompatActivity implements GetResult.MyListener {
    private static final String TAG = "FirstSigninActivity";
    private EditText fullName;
    private EditText birthday, firstSignAge;
    private EditText teL;
    private Button btn;

    java.util.Calendar myCal;
    String formattedDat;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    CustPrograssbar custPrograssbar;
    Spinner spinner;
    Spinner specialiteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_signin);

        btn = (Button) findViewById(R.id.confirmeBtn);
        fullName = (EditText) findViewById(R.id.firstSignFullName);
        birthday = (EditText) findViewById(R.id.firstSignBirthDay);
        teL = (EditText) findViewById(R.id.firstSignTel);
        custPrograssbar = new CustPrograssbar();
        firstSignAge = (EditText) findViewById(R.id.firstSignAge);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDat = df.format(c);
        myCal = Calendar.getInstance();

        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        specialiteList = (Spinner) findViewById(R.id.specialite_spinner);
        ArrayAdapter<CharSequence> adapterSpecialiteList = ArrayAdapter.createFromResource(this,
                R.array.specialite_spinner, android.R.layout.simple_spinner_item);
        adapterSpecialiteList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialiteList.setAdapter(adapterSpecialiteList);
        String newAccountType = spinner.getSelectedItem().toString();

        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.MONTH, monthOfYear);
                myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                getAge(year, monthOfYear, dayOfMonth);
            }

        };

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(FirstSigninActivity.this, 0, dateListener, myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH), myCal.get(Calendar.DATE)).show();
            }
        });

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinner.getSelectedItem().toString();
                Log.e(TAG, "onItemSelected:" + selected);
                if (selected.equals("Doctor")) {
                    specialiteList.setVisibility(View.VISIBLE);
                } else {
                    specialiteList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                specialiteList.setVisibility(View.GONE);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custPrograssbar.progressCreate(FirstSigninActivity.this);
                custPrograssbar.setCancel(false);
                String fullname, birtDay, tel, type, specialite, age;

                birtDay = birthday.getText().toString();
                fullname = fullName.getText().toString();
                tel = teL.getText().toString();
                age = firstSignAge.getText().toString();

                type = spinner.getSelectedItem().toString();

                specialite = specialiteList.getSelectedItem().toString();

//              add data to api
                updatephase2(fullname, "address", tel, birtDay, age, specialite, type);

            }


        });
    }

    private void updatephase2(String fullname, String address, String tel, String birtDay, String age, String specialite, String type) {


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", currentUser.getEmail());
            jsonObject.put("name", fullname);
            jsonObject.put("dob", birtDay);
            jsonObject.put("age", age);
            if (type.equalsIgnoreCase("Patient")) {
                jsonObject.put("specialization", "No match");
                jsonObject.put("role", "patient");
                jsonObject.put("status", 1);

            } else {
                jsonObject.put("specialization", specialite);
                jsonObject.put("role", "doctor");
                jsonObject.put("status", "0");

            }

            jsonObject.put("mobile", tel);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().registerphase2((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.onNCHandle(call, "updatephase2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void updateLabel() {
        String myFormat = "YYYY-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat(myFormat);
        }
        birthday.setText(sdf.format(myCal.getTime()));

    }

    private void getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        String myFormat = "YYYY-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat(myFormat);
        }
        birthday.setText(sdf.format(myCal.getTime()));

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        firstSignAge.setText(ageS);
        //return ageS;
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        if (callNo.equalsIgnoreCase("updatephase2")) {
            custPrograssbar.close();
            Gson gson = new Gson();

            ResponseBasic responseBasic = gson.fromJson(result.toString(), ResponseBasic.class);

            if (responseBasic.getResult().equals("true")) {

                String d_id =  responseBasic.getResponseMsg();

                Toast.makeText(this, "docid : "+responseBasic.getResponseMsg(), Toast.LENGTH_LONG).show();

                String fullname, birtDay, tel, type, specialite, age;

                birtDay = birthday.getText().toString();
                fullname = fullName.getText().toString();
                tel = teL.getText().toString();
                age = firstSignAge.getText().toString();

                type = spinner.getSelectedItem().toString();
                specialite = specialiteList.getSelectedItem().toString();

                UserHelper.addUser(fullname, birtDay, tel, type, birtDay, age);

                if (type.equals("Patient")) {

                    PatientHelper.addPatient(fullname, "adress", tel, birtDay, age);
                    System.out.println("Add patient " + fullname + " to patient collection");

                } else {
                    DoctorHelper.addDoctor(fullname, "adress", tel, specialite, birtDay, age,d_id);
                }

                Intent k = new Intent(FirstSigninActivity.this, MainActivity.class);
                startActivity(k);

            }

        }

    }
}
