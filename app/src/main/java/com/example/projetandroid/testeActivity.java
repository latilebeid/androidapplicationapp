package com.example.projetandroid;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
public class testeActivity extends AppCompatActivity {
    private TextView text_view;
    private TextView text_view1;
    private TextView text_view2;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDbRef;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView teste_profileIv;
    TextView teste_nameTv,teste_userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;
    //for checking if userhas seen message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;
     String hisUid;
     String myUid;
     String hisImage;
     String name;
    TextView test_userStatusTv;
    private TextView userStatusTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste3);
        toolbar = findViewById(R.id.teste_toolbar);
        messageEt = findViewById(R.id.teste_messageEt);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView=findViewById(R.id.teste_recyclerviewr);
        teste_nameTv = findViewById(R.id.test_tv);
        teste_profileIv= findViewById(R.id.teste_profileIV);
        sendBtn = findViewById(R.id.teste_sendBtn);
        test_userStatusTv= findViewById(R.id.test_userStatusTv);
        //Layout (LinearLayout) for RecyclerView

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        //recyclerview properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        Intent intent= getIntent();

            hisUid = intent.getStringExtra("hisUid");
            //   text_view.setText((hisUid));
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            usersDbRef = firebaseDatabase.getReference("Users");
            //search user to get that user's info
            Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);
            userQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Check until required info is received
                    for(DataSnapshot ds: snapshot.getChildren()){
                        String name =""+ ds.child("name").getValue();
                        hisImage =""+ ds.child("Image").getValue().toString();

                        // set data
                        teste_nameTv.setText(name);
                        try {
                            //if image
                            Picasso.get().load(hisImage).into(teste_profileIv);
                        } catch (Exception e) {
                            Picasso.get().load(R.drawable.ic_baseline_face_24).into(teste_profileIv);
                        }
                        //get value of onlineStatus
                        String onlineStatus = ""+ ds.child("onlineStatus").getValue();
                        if(onlineStatus.equals("online")){
                            Toast.makeText(testeActivity.this, "online ", Toast.LENGTH_SHORT).show();
                            test_userStatusTv.setText(onlineStatus);

                        }
                        else{
                            Toast.makeText(testeActivity.this, "online "+hisImage, Toast.LENGTH_SHORT).show();
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus)); /* this code is first error*/
                            String dataTime = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                            // Toast.makeText(testeActivity.this, " "+dateTime, Toast.LENGTH_SHORT).show();
                            test_userStatusTv.setText("last seen at : "+dataTime);


                        }



                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  Toast.makeText(testeActivity.this, "btn ", Toast.LENGTH_SHORT).show();
                    //get text from edit text
                    String message = messageEt.getText().toString().trim();
                 //   Toast.makeText(testeActivity.this, "btn " + hisImage, Toast.LENGTH_SHORT).show();
                    //check if text is empty or not
                    if (TextUtils.isEmpty(message)) {
                        //text empty
                        Toast.makeText(testeActivity.this, "cannot send empty message", Toast.LENGTH_SHORT).show();
                    } else {
                        //text not empty
                        sendMessage(message);

                    }
                }
            });
        Toast.makeText(this, "image houna "+ hisImage, Toast.LENGTH_SHORT).show();
          readMessages();
          seenMessage();
        }




    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelChat chat =ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object>hasSeenHashMap= new HashMap<>();
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
       // Toast.makeText(testeActivity.this, "latifa  111: "+hisImage, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(testeActivity.this, "image : "+hisImage, Toast.LENGTH_SHORT).show();
                    adapterChat = new AdapterChat(testeActivity.this,chatList,hisImage);
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
            startActivity(new Intent(this,LoginActivity.class));
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