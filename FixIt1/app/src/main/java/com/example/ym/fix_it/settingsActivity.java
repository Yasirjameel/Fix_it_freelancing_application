package com.example.ym.fix_it;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class settingsActivity extends AppCompatActivity {

    private EditText bio,country,gender,phone;
    private Button updateaccount;
    private DatabaseReference settingsref;
    private FirebaseAuth mauth;
    private String currentuserid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mauth= FirebaseAuth.getInstance();
        currentuserid = mauth.getCurrentUser().getUid();
        settingsref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserid);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Profile");

      bio = (EditText) findViewById(R.id.status);
      country = (EditText) findViewById(R.id.country1);
      gender = (EditText) findViewById(R.id.gender1);
      phone = (EditText) findViewById(R.id.phone1);
      updateaccount = (Button) findViewById(R.id.updatesettings);


      settingsref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

         if(dataSnapshot.exists())
         {
             String mybio = dataSnapshot.child("status").getValue().toString();
             String mycountry = dataSnapshot.child("country").getValue().toString();
             String mygender = dataSnapshot.child("gender").getValue().toString();
             String mynumber = dataSnapshot.child("phonenum").getValue().toString();

             bio.setText(mybio);
             country.setText(mycountry);
             gender.setText(mygender);
             phone.setText(mynumber);
         }

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

    updateaccount.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            
            validateaccountinfo();
            
        }
    });

    }

    private void validateaccountinfo() {

        String status = bio.getText().toString();
        String counti = country.getText().toString();
        String gendr = gender.getText().toString();
        String num = phone.getText().toString();

        if(TextUtils.isEmpty(status))
        {
            Toast.makeText(settingsActivity.this,"Please write your bio", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(counti))
        {
            Toast.makeText(settingsActivity.this,"Please write your country", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(gendr))
        {
            Toast.makeText(settingsActivity.this,"Please write your gender", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(num))
        {
            Toast.makeText(settingsActivity.this,"Please write your phone number", Toast.LENGTH_SHORT).show();
        }
        else
        {
            updateinfo(status,counti,gendr,num);
        }
    }

    private void updateinfo(String status, String counti, String gendr, String num) {
        HashMap usermap = new HashMap();
        usermap.put("status", status);
        usermap.put("country", counti);
        usermap.put("gender", gendr);
        usermap.put("phonenum", num);
        settingsref.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isSuccessful())
                {

                    Sendusertomainactivity();
                    Toast.makeText(settingsActivity.this,"Account settings updated sucessfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(settingsActivity.this,"Error occured while updating account settings", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
    private void Sendusertomainactivity() {
        Intent mainintent = new Intent(settingsActivity.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}
