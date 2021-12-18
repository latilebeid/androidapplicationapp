package com.example.projetandroid;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    ProgressDialog pd ;

    // view from xml

    ImageView avatarIv,coverIv;
    TextView nameTv, emailTv, phoneTv;
    FloatingActionButton fab ;
    private static final int CAMERA_REQUEST_CODE = 100 ;
    private static final int STORAGE_REQUEST_CODE = 200 ;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300 ;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400 ;
    String cameraPermissions[];
    String storagePermissions[];
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
        databaseReference = firebaseDatabase.getReference("Users");
        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        avatarIv = view.findViewById(R.id.avatarIv);
        coverIv = view.findViewById(R.id.coverIv);
        nameTv = view.findViewById(R.id.nametv);
        emailTv = view.findViewById(R.id.emailtv);
        phoneTv = view.findViewById(R.id.phonetv);
        fab = view.findViewById(R.id.fabid);

        pd = new ProgressDialog(getActivity());

        /*No have to get info of currently signed in user.we can get it using user's email or uid ,I'm gonna retrieve user detail using email*/
        /* By using orderByChild query we will show the detail from a node whose Key named email has value equal to currently signed in email.
         It will search all nodes , where the Key matches it will get its detail  */

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String email = "" + ds.child("email").getValue();
                    String image = "" + ds.child("Image").getValue();
                    String cover = "" + ds.child("cover").getValue();
                    //set data
                    nameTv.setText(name);
                    phoneTv.setText(phone);
                    emailTv.setText(email);
                    try {
                        //if image
                        Picasso.get().load(image).into(avatarIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_baseline_face_24).into(avatarIv);
                    }
                    try {
                        //if cover
                        Picasso.get().load(cover).into(coverIv);
                    } catch (Exception e) {
                        Picasso.get().load(cover).into(avatarIv);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });
        return view;
    }

    private void showEditProfileDialog() {
        String options[] = {"Edit Profile Picture","Edit Cover Photo","Edit Name","Edit Phone"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("choose Action");
        //set itemes to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
           if(i==0){
             //Edit Profile Picture
             pd.setTitle("Updating Profile Picture");
             showImagePicDialog();
           }
           else if (i==1){
               pd.setTitle("Updating Cover photo");
           }
           else if (i==2){
               pd.setTitle("Updating Name");
           }
           else if (i==3){
               pd.setTitle("Updating phone ");
           }

            }
        });
        builder.create().show();
    }
    private boolean checkCameraPermission(){
        //check if storage permission is enabled or not
        // return a boolean
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
     return result;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(),cameraPermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkstoragePermission(){

        //check if storage permission is enabled or not
        // return a boolean

        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(),storagePermissions,STORAGE_REQUEST_CODE);
    }
    private void showImagePicDialog() {

            String options[] = {"Camera","Gallery"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Pick Image From");
            //set itemes to dialog
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i==0){
                        //Camera clicked
                        pd.setTitle("Updating Profile Picture");
                        showImagePicDialog();
                    }
                    else if (i==1){
                        //gallery clicked
                        pd.setTitle("Updating Cover photo");
                    }

                }
            });
            builder.create().show();
        }

    }
