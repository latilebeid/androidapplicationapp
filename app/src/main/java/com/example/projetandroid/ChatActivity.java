package com.example.projetandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.projetandroid.adapters.AdapterChat;
import com.example.projetandroid.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    //views from xml
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv,userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;
    //Firebase auth
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;
    //for checking if userhas seen message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    //List<ModelChat> chatList;
   // AdapterChat adapterChat;
    String hisUid;
    String myUid;
    String hisImage;
    List<ModelChat> chatList;
    public AdapterChat adapterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chat_toolbar);
       // setSupportActionBar(toolbar);
        toolbar.setTitle("userStatusTv");
        recyclerView = findViewById(R.id.chat_recyclerview);
        profileIv= findViewById(R.id.chat_profileIV);
        nameTv = findViewById(R.id.chat_tv);
        userStatusTv = findViewById(R.id.chat_userStatusTv);
        messageEt = findViewById(R.id.chat_messageEt) ;
        sendBtn = findViewById(R.id.chat_sendBtn);
        Toast.makeText(this, "ici1 ", Toast.LENGTH_SHORT).show();
        //Layout (LinearLayout) for RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        Toast.makeText(this, "ici2 ", Toast.LENGTH_SHORT).show();
        //recyclerview properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        /*On clicking user from users list we have passed that user's UID using intent
         * So got that uid here to get the profile picture,name and start chat with that user */
        Intent intent= getIntent();
        if(intent != null){
            hisUid = intent.getStringExtra("hisUid");
        }else{
            Toast.makeText(this, "user id error ", Toast.LENGTH_SHORT).show();
        }

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");

        //search user to get that user's info
        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);
        //get user picture and name
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Check until required info is received
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    String name =""+ ds.child("name").getValue();
                     hisImage  =""+ ds.child("Image").getValue();

                    // set data
                    nameTv.setText(name);
                    try {
                        //image received ,set it to imageview in toolbar
                          Picasso.get().load(hisImage).placeholder(R.drawable.ic_baselinev_face_24).into(profileIv);
                    }
                    catch (Exception e){
                        // there is exception getting picture, set default picture
                           Picasso.get().load(R.drawable.ic_baselinev_face_24).into(profileIv);

                    }
                    //get value of onlineStatus
                  String onlineStatus = ""+ ds.child("onlineStatus").getValue();
                   if(onlineStatus.equals("online")){
                        userStatusTv.setText(onlineStatus);
                    }else{
                       Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                       cal.setTimeInMillis(Long.parseLong(onlineStatus)); /* this code is first error*/
                       String dataTime = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                       // Toast.makeText(testeActivity.this, " "+dateTime, Toast.LENGTH_SHORT).show();
                       userStatusTv.setText("last seen at : "+dataTime);

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //click button to send message
        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //get text from edit text
                String message = messageEt.getText().toString().trim();
                //check if text is empty or not
                if(TextUtils.isEmpty(message)){
                    //text empty
                    Toast.makeText(ChatActivity.this,"cannot send empty message",Toast.LENGTH_SHORT).show();
                }
                else {
                    //text not empty
                    sendMessage(message);

                }

            }
        });
        Toast.makeText(this, " error "+ hisUid, Toast.LENGTH_SHORT).show();
        readMessages();
        Toast.makeText(this, " error1 "+hisImage, Toast.LENGTH_SHORT).show();

      seenMessage();
    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        Toast.makeText(this, "seen"+myUid, Toast.LENGTH_SHORT).show();
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelChat chat =ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap= new HashMap<>();
                        hasSeenHashMap.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                       chatList.add(chat);
                    }
                    //adapter
                   adapterChat = new AdapterChat(ChatActivity.this,chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String message) {
        /* "Chats" node will be created that will contain all chats */
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);

        databaseReference.child("chats").push().setValue(hashMap);
        //reset editText after sending message
        messageEt.setText("");


    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            //user is signed in stay here
            //set email of logged in user
            //mProfileTv.setText(user.getEmail());
            myUid = user.getUid();// currently signed in user's uid


        } else{
            //user not signed in, go to main activity
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }
    private void checkOnlineStaus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update value of onlineStatus of current user
        dbRef.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        //set online
        checkOnlineStaus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //get timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        //set offline with last seen time stamp
        checkOnlineStaus(timestamp);
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        //set online
        checkOnlineStaus("online");
        super.onResume();
    }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        //hide searchview , as we don't need it here
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}