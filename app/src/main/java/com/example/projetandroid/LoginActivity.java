package com.example.projetandroid;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
public class LoginActivity extends AppCompatActivity {
    TextInputEditText etLoginEmail;
    TextInputEditText etLoginPassword;
    TextView tvRegisterHere,RecoverPassTv;
    Button btnLogin;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPass);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);
        btnLogin = findViewById(R.id.btnLogin);
        //init progress dialog
        pd = new ProgressDialog(this);
        RecoverPassTv=findViewById(R.id.logrecovertv);
        mAuth = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(LoginActivity.this, "latiffa1 ", Toast.LENGTH_LONG).show();
                String email = etLoginEmail.getText().toString();
                String password = etLoginPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    etLoginEmail.setError("Email cannot be empty");
                    etLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    etLoginPassword.setError("Password cannot be empty");
                    etLoginPassword.requestFocus();
                } else{
                  //  Toast.makeText(LoginActivity.this, "latiffa2 ", Toast.LENGTH_LONG).show();
                   // Toast.makeText(LoginActivity.this, "latiffa2 "+email + "pass : "+ password, Toast.LENGTH_LONG).show();
                    loginUser(email, password);}
            }



        });
        tvRegisterHere.setOnClickListener(view ->{
            startActivity(new Intent(LoginActivity.this, RegistrerActivity.class));
            finish();
        });
        RecoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialog();
            }
        });

    }
    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set layout linear layout
        LinearLayout linearLayout = new LinearLayout(this);
        builder.setTitle("Recover Password");
        // views to set in dialo
        final EditText emailet = new EditText(this);
        emailet.setHint("Email");
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        /* n'acceptez que 10 lettres */
        emailet.setMinEms(16);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        //buttons Recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String emaildialog = emailet.getText().toString().trim();
                beginRecovry(emaildialog);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dismiss dialog
                pd.dismiss();
            }
        });
      //show dialog
      builder.create().show();
    }

    private void beginRecovry(String emaildialog) {
       // pd.show();
       // pd.setMessage("sending email...");
        mAuth.sendPasswordResetEmail(emaildialog).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              //  pd.dismiss();
           if(task.isSuccessful()){
               Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
           }
           else{

               Toast.makeText(LoginActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
           }
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                //pd.dismiss();
                //get and show proper error message
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email , String password) {

        pd.show();
        pd.setMessage("logging in...");
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                              //  Toast.makeText(LoginActivity.this, "succes", Toast.LENGTH_LONG).show();

                                FirebaseUser user = mAuth.getCurrentUser();
                                pd.dismiss();
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    Toast.makeText(LoginActivity.this, "succes1", Toast.LENGTH_LONG).show();
                                    // get user email and uid from auth

                                    String email = user.getEmail();
                                    String uid = user.getUid();
                                    // when user is registred store user info in firebase realTime databasetoo
                                    // Using HashMap
                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    // put info in HashMap
                                    hashMap.put("email", email);
                                    hashMap.put("uid", uid);
                                    hashMap.put("name", "");
                                    hashMap.put("phone", "");
                                    hashMap.put("Image", "");
                                    hashMap.put("cover", "");
                                    //fireBase database instance
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    //path to store user data named "Users"
                                    DatabaseReference reference = database.getReference("Users");
                                    //put data within hashmap in database
                                    reference.child(uid).setValue(hashMap);

                                }
                             //   startActivity(new Intent(LoginActivity.this, teste.class));
                                Toast.makeText(LoginActivity.this, ""+user.getEmail(), Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, DashbordActivity.class));
                                finish();
                            } else

                                    Toast.makeText(LoginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }




                    });

                    }








}
