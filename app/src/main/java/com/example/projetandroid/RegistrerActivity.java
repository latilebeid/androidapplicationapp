package com.example.projetandroid;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrerActivity extends AppCompatActivity {

    TextInputEditText etRegEmail;
    TextInputEditText etRegPassword;
    TextView tvLoginHere;
    Button btnRegister;
    FirebaseAuth mAuth;
    private Dialog progressDialog;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrer);

        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.btnRegister);
        pd = new ProgressDialog(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Registrer");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view ->{
            createUser();
        });

        tvLoginHere.setOnClickListener(view ->{
            startActivity(new Intent(RegistrerActivity.this, LoginActivity.class));
        });
    }

    private void createUser(){
        pd.show();
        pd.setMessage("Registrer...");
        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
            pd.dismiss();
        }else if (TextUtils.isEmpty(password)){
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
            pd.dismiss();
        }else{
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        //sign in success, dismiss dialog and start register activity
                        pd.dismiss();
                        //sign in success, update UI with the signed in user information
                        FirebaseUser user = mAuth.getCurrentUser();
                        // get user email and uid from auth
                        String email = user.getEmail();
                        String uid = user.getUid();
                        // when user is registred store user info in firebase realTime databasetoo
                        // Using HashMap
                        HashMap<Object, String> hashMap = new HashMap<>();
                        // put info in HashMap
                        hashMap.put("email", email);
                        hashMap.put("uid", uid);
                        hashMap.put("name","");
                        hashMap.put("phone", "");
                        hashMap.put("Image", "");
                        hashMap.put("cover", "");
                        //fireBase database instance
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        //path to store user data named "Users"
                        Toast.makeText(RegistrerActivity.this, "je suis ici", Toast.LENGTH_SHORT).show();
                        DatabaseReference reference = database.getReference("Users");
                        Toast.makeText(RegistrerActivity.this, "je suis ici1", Toast.LENGTH_SHORT).show();
                        //put data within hashmap in database
                        reference.child(uid).setValue(hashMap);
                        Toast.makeText(RegistrerActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrerActivity.this, DashbordActivity.class));
                        finish();
                    }else{
                        pd.dismiss();
                        Toast.makeText(RegistrerActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}