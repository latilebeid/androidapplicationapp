package com.example.projetandroid;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;
public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context ;
    List<Model_users> usersList;

    //constructeur


    public AdapterUsers(Context context, List<Model_users> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup ViewGroup, int i) {
        //inflate layout (row_user
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, ViewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
    //recuperation de données
        String userImage = usersList.get(i).getImage();
        String userName = usersList.get(i).getName();
        String userEmail = usersList.get(i).getEmail();
        Toast.makeText(context, ""+ userEmail, Toast.LENGTH_SHORT).show();
    //set data
        holder.mNameTv.setText(userName);
        holder.mEmailTv.setText(userEmail);
        try{
            Picasso.get().load(userImage).placeholder(R.drawable.ic_baselinev_face_24).into(holder.mAvatarIv);
        }
        catch (Exception e) {

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+ userEmail, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return usersList.size();
    }
    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv , mEmailTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init views
            mAvatarIv = itemView.findViewById(R.id.profile_image);
            mNameTv = itemView.findViewById(R.id.row_user_nom);
            mEmailTv = itemView.findViewById(R.id.row_user_email);
        }
    }
}
