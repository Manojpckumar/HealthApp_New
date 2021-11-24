package com.manojpc.healthcareapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.manojpc.healthcareapp.DoctorRequest;
import com.manojpc.healthcareapp.R;
import com.manojpc.healthcareapp.RecyclerViewClickInterface;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.model.AllRole;
import com.manojpc.healthcareapp.model.Example;
import com.manojpc.healthcareapp.model.ResponseBasic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class DoctorRequestAdapter extends RecyclerView.Adapter<DoctorRequestAdapter.MyviewHolder> {

    Context context;
    List<AllRole> doctorList;
    RecyclerViewClickInterface recyclerViewClickInterface;

    public DoctorRequestAdapter(Context context, List<AllRole> doctorList, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.doctorList = doctorList;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public DoctorRequestAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_request,
                parent, false);
        return new MyviewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull DoctorRequestAdapter.MyviewHolder holder, int position) {

        AllRole users = doctorList.get(position);

        holder.tv_docName.setText(users.getName());
        holder.tv_docEmail.setText(users.getEmail());
        holder.tv_docSpec.setText(users.getSpecialization());

        if(users.getStatus().equals("0"))
        {
            holder.btn_approve.setText("Approve");
            holder.btn_approve.setBackgroundColor(Color.RED);

        }
        else
            {
            holder.btn_approve.setVisibility(View.GONE);
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.tv_status.setText("Approved");
            holder.tv_status.setBackgroundTintList(context.getResources().getColorStateList(R.color.green));

        }


    }


    @Override
    public int getItemCount() {
        return doctorList.size();
    }



    public class MyviewHolder extends RecyclerView.ViewHolder {

        TextView tv_docName,tv_docEmail,tv_docSpec,tv_status;
        AppCompatButton btn_approve;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            tv_docName = itemView.findViewById(R.id.tv_docName);
            tv_docEmail = itemView.findViewById(R.id.tv_docEmail);
            tv_docSpec = itemView.findViewById(R.id.tv_docSpec);
            btn_approve = itemView.findViewById(R.id.btn_approve);
            tv_status = itemView.findViewById(R.id.tv_status);

            String buttonText = btn_approve.getText().toString();


                btn_approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                        recyclerViewClickInterface.onItemClick(getAdapterPosition(),"APPROVE");

                    }
                });

        }
    }
}
