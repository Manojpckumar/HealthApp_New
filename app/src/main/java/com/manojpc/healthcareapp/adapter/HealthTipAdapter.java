package com.manojpc.healthcareapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manojpc.healthcareapp.HealthTipActivity;
import com.manojpc.healthcareapp.R;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.model.ResponseBasic;
import com.manojpc.healthcareapp.model.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class HealthTipAdapter extends RecyclerView.Adapter<HealthTipAdapter.MyViewHolder> implements GetResult.MyListener  {

    List<Task> list = new ArrayList();
    Context context;
    String userRole;

    public HealthTipAdapter(HealthTipActivity healthTipActivity, List<Task> health, String userRole) {

        this.context = healthTipActivity;
        this.list = health;
        this.userRole = userRole;

    }

    @NonNull
    @Override
    public HealthTipAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.blog_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthTipAdapter.MyViewHolder holder, int position) {

        Task model = list.get(position);

        holder.tv_title.setText(model.getTitle());
        holder.tv_desc.setText(model.getDescription());
        holder.tv_blogdateandtime.setText(model.getApDate());

        Glide.with(context).load(APIClient.baseUrl+model.getImage()).into(holder.iv_tipImng);

        if(model.getAgeCat().equals("0"))
        {
            if(model.getStatus().equals("0"))
            {
                holder.btn_progress.setVisibility(View.VISIBLE);
                holder.btn_progress.setText("Start");
                holder.btn_progress.setBackgroundResource(R.drawable.button_home);
                holder.btn_progress.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            }
            else if(model.getStatus().equals("1"))
            {
                holder.btn_progress.setVisibility(View.VISIBLE);
                holder.btn_progress.setText("Started");
                holder.btn_progress.setBackgroundResource(R.drawable.button_home);
                holder.btn_progress.setBackgroundColor(context.getResources().getColor(R.color.colorAccent2));
            }
            else if(model.getStatus().equals("2"))
            {
                holder.btn_progress.setVisibility(View.VISIBLE);
                holder.btn_progress.setText("Completed");
                holder.btn_progress.setBackgroundResource(R.drawable.button_home);
                holder.btn_progress.setBackgroundColor(context.getResources().getColor(R.color.green));
            }
            else
            {
                holder.btn_progress.setVisibility(View.GONE);
            }


        }
        else
        {
            holder.btn_progress.setVisibility(View.GONE);
        }


//        clicks
        if( holder.btn_progress.getText().toString().equalsIgnoreCase("Start"))
        {

            holder.btn_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    updateHealthTipStatus(model.gettId(),"1");

                }
            });

        }
        else if(holder.btn_progress.getText().toString().equalsIgnoreCase("Started"))
        {
            holder.btn_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    updateHealthTipStatus(model.gettId(),"2");

                }
            });
        }
        else
        {
            holder.btn_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(context, "Already Completed", Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    private void updateHealthTipStatus(String t_id, String s) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("t_id", t_id);
            jsonObject.put("status", s);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().updateStatus((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(HealthTipAdapter.this);
            getResult.onNCHandle(call, "updateTipsstatus");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void callback(JsonObject result, String callNo) {

        if (callNo.equalsIgnoreCase("updateTipsstatus")) {

            Gson gson = new Gson();

            ResponseBasic responseBasic = gson.fromJson(result.toString(),ResponseBasic.class);

            if(responseBasic.getResult().equals("true"))
            {
                Toast.makeText(context, "Status updated successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, responseBasic.getResponseMsg(), Toast.LENGTH_SHORT).show();
            }

        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title,tv_blogdateandtime,tv_desc;
        ImageView iv_tipImng;
        Button btn_progress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_desc = itemView.findViewById(R.id.tv_desc);
            tv_blogdateandtime = itemView.findViewById(R.id.tv_blogdateandtime);

            iv_tipImng = itemView.findViewById(R.id.iv_blogimg);
            btn_progress = itemView.findViewById(R.id.btn_progress);
        }
    }
}

