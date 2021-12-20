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
        actionBar.setTitle(" Profile");
        //init
        firebaseAuth = FirebaseAuth.getInstance();
        //bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        //home fragment transaction(default,on start
        actionBar.setTitle("Home");
        HomeFragment homeFragmentI = new HomeFragment();
        FragmentTransaction fragmentHomeTransaction = getSupportFragmentManager().beginTransaction();
        fragmentHomeTransaction.replace(R.id.content,homeFragmentI,"");
        fragmentHomeTransaction.commit();
    }


    private MenuItem menuItem;
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //handle Item click

                    switch (item.getItemId()){
                        case R.id.navigation_home:
                            //home fragment transaction
                            actionBar.setTitle("Home");
                            HomeFragment homeFragment = new HomeFragment();
                            FragmentTransaction fragmentHomeTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentHomeTransaction.replace(R.id.content,homeFragment,"");
                            fragmentHomeTransaction.commit();
                            return true;

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }
    /* handle menu email clicks */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}