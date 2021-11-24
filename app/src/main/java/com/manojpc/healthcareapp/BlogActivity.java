package com.manojpc.healthcareapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.Utils.CustPrograssbar;
import com.manojpc.healthcareapp.adapter.BlogAdapter;
import com.manojpc.healthcareapp.adapter.HealthTipAdapter;
import com.manojpc.healthcareapp.model.Blog;
import com.manojpc.healthcareapp.model.Example;
import com.manojpc.healthcareapp.model.ResponseBasic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class BlogActivity extends AppCompatActivity implements GetResult.MyListener,RecyclerViewClickInterface {

    RecyclerView rcv_blogposts;
    FloatingActionButton fab_addblogpost;
    List blog = new ArrayList();
    List<Blog> blogList;
    TextView tv_nothing;

    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    String userId;
    CustPrograssbar custPrograssbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        rcv_blogposts = findViewById(R.id.rcv_blogposts);
        fab_addblogpost = findViewById(R.id.fab_addblogposts);
        tv_nothing = findViewById(R.id.tv_nothing);

        custPrograssbar = new CustPrograssbar();

        fab_addblogpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BlogActivity.this, AddBlog.class);
                startActivity(intent);

            }
        });

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        custPrograssbar.progressCreate(BlogActivity.this);
        custPrograssbar.setCancel(false);
        getDetailsByEmail(currentUser.getEmail());

    }

    private void getDetailsByEmail(String email) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().statusbymail((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.onNCHandle(call, "statusbymail");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getBlogsFromApi(String uid) {

        userId = uid;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("u_id", "0");

            JsonParser jsonParser = new JsonParser();
            //Toast.makeText(this, "You are registering with us ", Toast.LENGTH_SHORT).show();
            Call<JsonObject> call = APIClient.getInterface().getAllBlogs((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(BlogActivity.this);
            getResult.onNCHandle(call, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void callback(JsonObject result, String callNo) {

        if (callNo.equalsIgnoreCase(userId)) {

            Log.d("user******",userId);

            Gson gson = new Gson();

            Example example = gson.fromJson(result.toString(),Example.class);

            blogList = new ArrayList<>();

            if(example.getResultData().getBlogs() == null)
            {
                tv_nothing.setVisibility(View.VISIBLE);
            }
            else
            {
                blogList.addAll(example.getResultData().getBlogs());

                BlogAdapter adapter = new BlogAdapter(this,blogList,userId,this);

                rcv_blogposts.setLayoutManager(new LinearLayoutManager(BlogActivity.this));

                rcv_blogposts.setAdapter(adapter);
                custPrograssbar.close();
            }

        }

        else if (callNo.equalsIgnoreCase("statusbymail")) {

            Gson gson = new Gson();

            Example example = gson.fromJson(result.toString(), Example.class);

            if (example.getResult().equals("true")) {

                String userId = example.getResultData().getUserDetails().getId();
                Log.d("userId5555000",userId);

                getBlogsFromApi(userId);
            }
        }

       else if (callNo.equalsIgnoreCase("del_wrt_blogs")) {

            Gson gson = new Gson();

            Example example = gson.fromJson(result.toString(), Example.class);

            if (example.getResult().equals("true")) {

                Toast.makeText(BlogActivity.this, "Blog Deleted Successfully", Toast.LENGTH_SHORT).show();

                getDetailsByEmail(currentUser.getEmail());


            }
        }


    }

    @Override
    public void onItemClick(int position, String chk) {

        if (chk.equalsIgnoreCase("DELETE")) {
//            Toast.makeText(this, "EDIT" + doctorList.get(position).getId(), Toast.LENGTH_SHORT).show();
            custPrograssbar.progressCreate(BlogActivity.this);
            custPrograssbar.setCancel(false);
            deleteBlogFromApi(blogList.get(position).getuId());
        }

    }

    private void deleteBlogFromApi(String getuId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("blog_id", getuId);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().deleteBlog((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.onNCHandle(call, "del_wrt_blogs");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}