package com.example.projetandroid;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    // view from xml
    ImageView avatartIv;
    TextView nameTv, emailTv, phoneTv;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");

        //init views
        avatartIv = view.findViewById(R.id.avatarIv);
        nameTv = view.findViewById(R.id.nametv);
        emailTv = view.findViewById(R.id.emailtv);
        phoneTv = view.findViewById(R.id.phonetv);
        /*No have to get info of currently signed in user.we can get it using user's email or uid ,I'm gonna retrieve user detail using email*/
        /* By using orderByChild query we will show the detail from a node whose Key named email has value equal to currently signed in email.
         It will search all nodes , where the Key matches it will get its detail  */
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String email = "" + ds.child("email").getValue();
                    String image = "" + ds.child("image").getValue();
                    //set data
                    nameTv.setText(name);
                    nameTv.setText(phone);
                    nameTv.setText(email);
                    nameTv.setText(image);
                    try {
                        //if image
                        Picasso.get().load(image).into(avatartIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_baseline_add_a_photo_24).into(avatartIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }


}


