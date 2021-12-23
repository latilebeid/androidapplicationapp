package com.example.projetandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashbordActivity extends AppCompatActivity {

    //fireBase auth
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);
        //Actionbar and its title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        //init
        firebaseAuth = FirebaseAuth.getInstance();
        //bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        //profile fragment transaction(default,on start
        actionBar.setTitle("Profile");
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentProfileTransaction = getSupportFragmentManager().beginTransaction();
        fragmentProfileTransaction.replace(R.id.content,profileFragment,"");
        fragmentProfileTransaction.commit();
    }


    private MenuItem menuItem;
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //handle Item click

                    switch (item.getItemId()){

                        case R.id.navigation_profile:
                            //profile fragment transaction
                            actionBar.setTitle("Profile");
                            ProfileFragment profileFragment = new ProfileFragment();
                            FragmentTransaction fragmentProfileTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentProfileTransaction.replace(R.id.content,profileFragment,"");
                            fragmentProfileTransaction.commit();
                            return true;
                        case R.id.navigation_users:
                            //Users fragment transaction
                            //profile fragment transaction
                            actionBar.setTitle("Users");
                            UsersFragment usersFragment = new UsersFragment();
                            FragmentTransaction fragmentUsersTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentUsersTransaction.replace(R.id.content,usersFragment,"");
                            fragmentUsersTransaction.commit();
                            return true;
                        case R.id.navigation_chats:
                            //home fragment transaction
                            actionBar.setTitle("Chat");
                            fragment_chat_list chatFragment = new fragment_chat_list();
                            FragmentTransaction fragmentChatTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentChatTransaction.replace(R.id.content,chatFragment,"");
                            fragmentChatTransaction.commit();
                            return true;
                    }
                    return false;
                }
            };
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
            startActivity(new Intent(DashbordActivity.this,MainActivity.class));
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    protected void onStart(){
        // check on start of app
        checkUserStatus();
        super.onStart();
    }
    // inflate options menu

}