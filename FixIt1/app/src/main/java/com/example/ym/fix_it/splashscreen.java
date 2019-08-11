package com.example.ym.fix_it;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

       getSupportActionBar().hide();

        Thread background = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(5000);

                    Intent intent = new Intent(splashscreen.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        background.start();
    }
}
