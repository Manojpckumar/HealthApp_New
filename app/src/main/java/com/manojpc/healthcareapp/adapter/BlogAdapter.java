package com.manojpc.healthcareapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manojpc.healthcareapp.BlogActivity;
import com.manojpc.healthcareapp.R;
import com.manojpc.healthcareapp.RecyclerViewClickInterface;
import com.manojpc.healthcareapp.Retrofit.APIClient;
import com.manojpc.healthcareapp.Retrofit.GetResult;
import com.manojpc.healthcareapp.model.Blog;
import com.manojpc.healthcareapp.model.Example;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import retrofit2.Call;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.MyViewHolder> implements GetResult.MyListener {

    List<Blog> list = new ArrayList();
    Context context;
    String usid;
    String bId;
    BlogAdapter.MyViewHolder blogholder;
    RecyclerViewClickInterface recyclerViewClickInterface;


    public BlogAdapter(BlogActivity blogActivity, List<Blog> blog,String userId,RecyclerViewClickInterface recyclerViewClickInterface) {

        this.context = blogActivity;
        this.list = blog;
        this.usid = userId;
        this.recyclerViewClickInterface = recyclerViewClickInterface;

    }

    @NonNull
    @Override
    public BlogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.blog_card,parent,false);

        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BlogAdapter.MyViewHolder holder, int position) {

        Blog model = list.get(position);

        holder.tv_title.setText(model.getTitle());
        holder.tv_desc.setText(model.getDescription());
        holder.tv_blogdateandtime.setText(model.getPostDate());

        Glide.with(context).load(APIClient.baseUrl+model.getImage()).into(holder.iv_tipImng);

        if(Integer.parseInt(usid) == Integer.parseInt(model.getuId()))
        {
            holder.ib_delete.setVisibility(View.VISIBLE);
        }




    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void callback(JsonObject result, String callNo) {

        if (callNo.equalsIgnoreCase("del_wrt_blogs")) {

            Gson gson = new Gson();

            Example example = gson.fromJson(result.toString(), Example.class);

            if (example.getResult().equals("true")) {

                Toast.makeText(context, "Blog Deleted Successfully", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context,BlogActivity.class);
//                context.startActivity(intent);
                list.remove(blogholder);
                notifyItemMoved(blogholder.getAdapterPosition(),blogholder.getAdapterPosition());

            }
        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title,tv_blogdateandtime,tv_desc;
        ImageView iv_tipImng;
        Button ib_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_desc = itemView.findViewById(R.id.tv_desc);
            tv_blogdateandtime = itemView.findViewById(R.id.tv_blogdateandtime);

            iv_tipImng = itemView.findViewById(R.id.iv_blogimg);
            ib_delete = itemView.findViewById(R.id.ib_delete);

            ib_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //deleteBlogFromApi(model.getuId(),holder);
                    recyclerViewClickInterface.onItemClick(getAdapterPosition(),"DELETE");

                }
            });
        }
    }
}
