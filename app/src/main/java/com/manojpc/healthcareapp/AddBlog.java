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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.manojpc.healthcareapp.model.Example;
import com.manojpc.healthcareapp.model.ResponseBasic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;

public class AddBlog extends AppCompatActivity implements GetResult.MyListener {

    private final int CGALLERY = 1;
    String imgName,encodedImgName,userID;
    EditText ed_title,ed_desc,ed_date,ed_imgname;
    Button attach,submit;
    java.util.Calendar myCal;
    CustPrograssbar custPrograssbar;

    FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        requestPermissions();

        custPrograssbar = new CustPrograssbar();

        ed_title = findViewById(R.id.ed_title);
        ed_desc = findViewById(R.id.ed_desc);
        ed_date = findViewById(R.id.ed_apdate);
        ed_imgname = findViewById(R.id.ed_imgname);

        attach = findViewById(R.id.bt_attach);
        submit = findViewById(R.id.bt_submit);

        myCal = Calendar.getInstance();
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        getStatusBYEmail(currentUser.getEmail());

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

        ed_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddBlog.this, 0, dateListeners, myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH), myCal.get(Calendar.DATE)).show();
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, CGALLERY);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                custPrograssbar.progressCreate(AddBlog.this);
                custPrograssbar.setCancel(false);
                addBlogToAPI();

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
            getResult.setMyListener(AddBlog.this);
            getResult.onNCHandle(call, "statusbymail");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addBlogToAPI() {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("title", ed_title.getText().toString());
            jsonObject.put("description", ed_desc.getText().toString());
            jsonObject.put("image_name", imgName);
            jsonObject.put("image", encodedImgName);
            jsonObject.put("post_date", ed_date.getText().toString());
            jsonObject.put("u_id", userID);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().createBlog((JsonObject)
                    jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(AddBlog.this);
            getResult.onNCHandle(call, "addBlogs");
            Log.d("postingJSON", jsonParser.parse(jsonObject.toString()).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
                    //iv.setImageBitmap(bitmap);
                    encodedImgName = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                    imgName = String.valueOf(Calendar.getInstance().getTimeInMillis());
                    ed_imgname.setText(imgName);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
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

    private void updateLabels() {
        String myFormat = "YYYY-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat(myFormat);
        }
        ed_date.setText(sdf.format(myCal.getTime()));

    }

    @Override
    public void callback(JsonObject result, String callNo) {

        if (callNo.equalsIgnoreCase("statusbymail")) {

            Gson gson = new Gson();

            Example example = gson.fromJson(result.toString(), Example.class);

            userID = example.getResultData().getUserDetails().getId();

        }
        else if (callNo.equalsIgnoreCase("addBlogs")) {

            Gson gson = new Gson();

            ResponseBasic responseBasic = gson.fromJson(result.toString(),ResponseBasic.class);

            if(responseBasic.getResult().equals("true"))
            {
                custPrograssbar.close();
                Toast.makeText(this, "Blog added successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AddBlog.this,BlogActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                custPrograssbar.close();
                Toast.makeText(this, responseBasic.getResponseMsg(), Toast.LENGTH_SHORT).show();
            }


            //Example example = gson.fromJson(result.toString(), Example.class);



        }



    }
}