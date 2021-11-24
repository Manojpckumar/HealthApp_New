package com.manojpc.healthcareapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.manojpc.healthcareapp.AllUsers;
import com.manojpc.healthcareapp.R;
import com.manojpc.healthcareapp.model.AllRole;

import java.util.List;

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.MyViewHolder> {

    Context context;
    List<AllRole> list;

    public AllUserAdapter(AllUsers allUsers, List<AllRole> doctorList) {

        this.list = doctorList;
        this.context = allUsers;

    }

    @NonNull
    @Override
    public AllUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_request,
                parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUserAdapter.MyViewHolder holder, int position) {

        AllRole users = list.get(position);

        holder.tv_docName.setText(users.getName());
        holder.tv_docEmail.setText(users.getEmail());
        holder.tv_docSpec.setText(users.getSpecialization());

        holder.btn_approve.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_docName,tv_docEmail,tv_docSpec;
        AppCompatButton btn_approve;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_docName = itemView.findViewById(R.id.tv_docName);
            tv_docEmail = itemView.findViewById(R.id.tv_docEmail);
            tv_docSpec = itemView.findViewById(R.id.tv_docSpec);
            btn_approve = itemView.findViewById(R.id.btn_approve);
        }
    }
}
