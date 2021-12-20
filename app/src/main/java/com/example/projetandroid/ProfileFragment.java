package com.example.projetandroid;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.StorageReference;

import java.security.Key;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressDialog pd ;
    //storage
    StorageReference StorageReference;
    //path where images of user profile and cover will be stored
    String storagePath = "Users_Profile_cover_Imgs/";
    // view from xml

    ImageView avatarIv,coverIv;
    TextView nameTv, emailTv, phoneTv;
    FloatingActionButton fab ;
    private static final int CAMERA_REQUEST_CODE = 100 ;
    private static final int STORAGE_REQUEST_CODE = 200 ;
    private static final int IMAGE_PICK_GALLERY_CODE = 300 ;
    private static final int IMAGE_PICK_CAMERA_CODE = 400 ;
    String cameraPermissions[];
    String storagePermissions[];
    //uri of picked image
    Uri image_uri ;
    //for checking profile or cover photo
    String profileOrCoverPhot;
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
        StorageReference = FirebaseStorage.getInstance().getReference();
       // StorageReference StorageReference = FirebaseStorage.getInstance().getReference();
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
                 /*   try {
                        //if cover
                        Picasso.get().load(cover).into(coverIv);
                    } catch (Exception e) {
                        Picasso.get().load(cover).into(coverIv);
                    }*/


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
             profileOrCoverPhot="image";
             showImagePicDialog();
           }
           else if (i==1){
               pd.setTitle("Updating Cover photo");
               profileOrCoverPhot="cover";
               showImagePicDialog();
           }
           else if (i==2){
               pd.setTitle("Updating Name");
               showNamePhoneUpdateDialog("name");
           }
           else if (i==3){
               pd.setTitle("Updating phone ");
               showNamePhoneUpdateDialog("phone");
           }

            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(String key) {

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Update  "+key);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        EditText edittext = new EditText(getActivity());
        edittext.setHint("Entrer "+key);
        linearLayout.addView(edittext);
        linearLayout.setPadding(10,10,10,10);
        b.setView(linearLayout);
        b.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = edittext.getText().toString().trim();
                if(!TextUtils.isEmpty(value)){
                  pd.show();
                  HashMap<String,Object> result = new HashMap<>();
                  result.put(key,value);
                  databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void unused) {
                          pd.dismiss();
                          Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();
                      }
                  }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          pd.dismiss();
                          Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                  });
                }
                else{
                    Toast.makeText(getActivity(), "Please enter " + key, Toast.LENGTH_SHORT).show();
                }
            }
        });
        b.setNegativeButton("annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //create and show dialog
        b.create().show();

    }

    private boolean checkCameraPermission(){
        //check if storage permission is enabled or not
        // return a boolean
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission() {
        requestPermissions(cameraPermissions,STORAGE_REQUEST_CODE);
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
                        if(!checkCameraPermission()){
                            requestCameraPermission();
                        }
                        else {
                            pickfromCamera();
                        }
                    }
                    else if (i==1){
                        //gallery clicked
                        if(!checkstoragePermission()){
                            requestStoragePermission();
                        }
                        else {
                            pickfromGallery();
                        }
                        pd.setTitle("Updating  photo");
                    }

                }
            });
            builder.create().show();
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //handle requests allowed or denided
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //check if camera and storage allowed or not
                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted= grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){

                        pickfromCamera();
                    }
                    else{
                        Toast.makeText(getActivity(), "please enable camera & storage permission ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{

                if(grantResults.length >0){

                    boolean writeStorageAccepted= grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if( writeStorageAccepted){
                        pickfromGallery();
                    }
                    else{
                        Toast.makeText(getActivity(), "please enable storage permission ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
          break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //cette methode va etre appler apres aprer pick camera ou gallery
        if(resultCode == RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
               //Image picker depuis la gallery, get uri of image
                image_uri = data.getData();
                uploadProfilecoverphoto(image_uri);
            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE){
                //Image picker depuis la camera, get uri of image
                uploadProfilecoverphoto(image_uri);
            }
        }
    }

    private void uploadProfilecoverphoto(Uri uri) {
        pd.show();
        //path and name of image to be stored in firebase storage
        String filePathAndName = storagePath+ ""+profileOrCoverPhot+"_"+user.getUid();
        StorageReference storageReference2nd = StorageReference.child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                //check if image is uploaded or not
                if(uriTask.isSuccessful()){
                    //image uploaded
                    //add/update url dans bd users
                    HashMap<String, Object> results = new HashMap<>();

                    results.put(profileOrCoverPhot, downloadUri.toString());
                    databaseReference.child(user.getUid()).updateChildren(results)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Image Updated .....", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error Updating Image.....", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    //error
                    pd.dismiss();
                    Toast.makeText(getActivity(), "erreur", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void pickfromCamera() {
        //intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        //put image
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);


    }

    private void pickfromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }
    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            //set email of logged in user
            //mprofileTv.setText(user.getEmail());
        }
        else{
            //user not signed in ,go to main activity
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }
    }

}
