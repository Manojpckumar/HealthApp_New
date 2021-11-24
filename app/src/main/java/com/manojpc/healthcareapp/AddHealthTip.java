package com.manojpc.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.Utils.CustPrograssbar;

import com.manojpc.healthcareapp.model.AppointeeUser;
import com.manojpc.healthcareapp.model.Doctor;
import com.manojpc.healthcareapp.model.Example;
import com.manojpc.healthcareapp.model.ResponseBasic;
import com.manojpc.healthcareapp.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

public class AddHealthTip extends AppCompatActivity implements GetResult.MyListener {

    EditText edt_title,edt_desc,edt_dur,edt_imgname,edt_apdate;
    Spinner spn_userType,spn_users;
    Button btn_attach,btn_submit;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    List<String> patientList,patientIdList;
    java.util.Calendar myCal;
    CustPrograssbar custPrograssbar;
    LinearLayout iv_frame;
    ImageView iv_upload;

    List<String> ageList = new ArrayList<>();
    private final int CGALLERY = 1;
    String imgName,encodedImgName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference DoctorRef = db.collection("Doctor");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_health_tip);

        ageList.add("1-10");
        ageList.add("10-20");
        ageList.add("20-30");
        ageList.add("30-40");
        ageList.add("40-50");

        mAuth = FirebaseAuth.getInstance();
        myCal = Calendar.getInstance();

        custPrograssbar = new CustPrograssbar();

        edt_title = findViewById(R.id.edt_title);
        edt_desc = findViewById(R.id.edt_desc);
        edt_dur = findViewById(R.id.edt_dur);
        edt_imgname = findViewById(R.id.edt_imgname);
        edt_apdate = findViewById(R.id.edt_apdate);
        spn_userType = findViewById(R.id.spn_userType);
        spn_users = findViewById(R.id.spn_users);
        btn_attach = findViewById(R.id.btn_attach);
        btn_submit = findViewById(R.id.btn_submit);

        iv_upload = findViewById(R.id.iv_upload);
        iv_frame = findViewById(R.id.iv_frame);

        requestPermissions();

        spn_userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selected = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(AddHealthTip.this, selected, Toast.LENGTH_LONG).show();

                if(selected.equals("By Patient"))
                {
                    currentUser = mAuth.getCurrentUser();

                    DoctorRef.document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            Doctor doctor = documentSnapshot.toObject(Doctor.class);

                            Toast.makeText(AddHealthTip.this, "Doctor id : "+doctor.getD_id(), Toast.LENGTH_SHORT).show();
                            getPatientsFromApi(doctor.getD_id());

                        }
                    });
//
                }
                else {

                    ArrayAdapter<String> adapters = new ArrayAdapter<String>(AddHealthTip.this,android.R.layout.simple_spinner_item, ageList);
                    adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spn_users.setAdapter(adapters);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.MONTH, monthOfYear);
                myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                //getAge(year, monthOfYear, dayOfMonth);
            }

        };

        final DatePickerDialog.OnDateSetListener dateListeners = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.MONTH, monthOfYear);
                myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabels();
                //getAge(year, monthOfYear, dayOfMonth);
            }

        };

        edt_dur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddHealthTip.this, 0, dateListener, myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH), myCal.get(Calendar.DATE)).show();
            }
        });

        edt_apdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddHealthTip.this, 0, dateListeners, myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH), myCal.get(Calendar.DATE)).show();
            }
        });

        btn_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, CGALLERY);

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                custPrograssbar.progressCreate(AddHealthTip.this);
                custPrograssbar.setCancel(false);
                addNewHealthTipToApi();

            }
        });




    }

    private void addNewHealthTipToApi() {

        String typeUserType;

        currentUser = mAuth.getCurrentUser();

        DoctorRef.document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Doctor doctor = documentSnapshot.toObject(Doctor.class);

                Log.d("JSonValuePostApi100",edt_title.getText().toString());
                Log.d("JSonValuePostApi100",edt_desc.getText().toString());
                Log.d("JSonValuePostApi100",edt_dur.getText().toString());
                Log.d("JSonValuePostApi100",imgName);
                Log.d("JSonValuePostApi100",encodedImgName);
                Log.d("JSonValuePostApi100",patientIdList.get(spn_users.getSelectedItemPosition()));
                Log.d("JSonValuePostApi100",spn_users.getSelectedItem().toString());
                Log.d("JSonValuePostApi100",edt_apdate.getText().toString());
                Log.d("JSonValuePostApi100",doctor.getD_id());

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("title", edt_title.getText().toString());
                    jsonObject.put("description", edt_desc.getText().toString());
                    jsonObject.put("duration", edt_dur.getText().toString());
                    jsonObject.put("image_name", imgName);
                    jsonObject.put("image", encodedImgName);

                    if (spn_userType.getSelectedItem().toString().equals("By Patient")) {
                        jsonObject.put("u_id",patientIdList.get(spn_users.getSelectedItemPosition()));
                        jsonObject.put("age_cat", "no match");

                    } else if (spn_userType.getSelectedItem().toString().equals("By Age Group")){
                        jsonObject.put("u_id", "no match");
                        jsonObject.put("age_cat", spn_users.getSelectedItem().toString());
                    }
                    else
                    {
                        jsonObject.put("u_id", "0");
                        jsonObject.put("age_cat", "10-20");
                    }
                    jsonObject.put("ap_date", edt_apdate.getText().toString());
                    jsonObject.put("status", "1");
                    jsonObject.put("doc_id",doctor.getD_id());


                    JsonParser jsonParser = new JsonParser();
                    Call<JsonObject> call = APIClient.getInterface().createHealthTip((JsonObject)
                            jsonParser.parse(jsonObject.toString()));
                    GetResult getResult = new GetResult();
                    getResult.setMyListener(AddHealthTip.this);
                    getResult.onNCHandle(call, "addHealthtips");
                    Log.d("postingJSON",jsonParser.parse(jsonObject.toString()).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CGALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    // iv_frame.setVisibility(View.VISIBLE);
                    iv_upload.setImageBitmap(bitmap);
                    encodedImgName = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                    imgName = String.valueOf(Calendar.getInstance().getTimeInMillis());


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    private void getPatientsFromApi(String d_id) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("doctor_id", d_id);

            JsonParser jsonParser = new JsonParser();
            //Toast.makeText(this, "You are registering with us ", Toast.LENGTH_SHORT).show();
            Call<JsonObject> call = APIClient.getInterface().getPatientsById((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.onNCHandle(call, "getAppointeeUsers");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void  requestPermissions(){
        Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void callback(JsonObject result, String callNo) {

        if (callNo.equalsIgnoreCase("getAppointeeUsers")) {

            Gson gson = new Gson();
            Example example = gson.fromJson(result.toString(),Example.class);

            patientList = new ArrayList<>();
            patientIdList = new ArrayList<>();
            List<AppointeeUser> list =  example.getResultData().getAppointeeUsers();

            for (AppointeeUser model : list)
            {
                patientList.add(model.getName());
                patientIdList.add(model.getId());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, patientList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_users.setAdapter(adapter);

        }

        if (callNo.equalsIgnoreCase("addHealthtips")) {

            custPrograssbar.close();
            Gson gson = new Gson();

            ResponseBasic responseBasic = gson.fromJson(result.toString(), ResponseBasic.class);

            if (responseBasic.getResult().equals("true")) {

                Toast.makeText(this, "Health Tip Added Successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, responseBasic.getResponseMsg().toString(), Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void updateLabel() {
        String myFormat = "YYYY-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat(myFormat);
        }
        edt_dur.setText(sdf.format(myCal.getTime()));

    }

    private void updateLabels() {
        String myFormat = "YYYY-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat(myFormat);
        }
        edt_apdate.setText(sdf.format(myCal.getTime()));

    }



}