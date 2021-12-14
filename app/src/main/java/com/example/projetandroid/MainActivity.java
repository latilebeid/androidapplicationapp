package com.example.projetandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_registrer;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init veiws
        btn_registrer = findViewById(R.id.registrer_btn_registrer);
        btn_login = findViewById(R.id.registrer_btn_login);
        //Handle registrer button click
        btn_registrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  // start RegisterActivity
                startActivity(new Intent(MainActivity.this,RegistrerActivity.class));
            }
        });
        //Handle login button click
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }

        });
    }
}