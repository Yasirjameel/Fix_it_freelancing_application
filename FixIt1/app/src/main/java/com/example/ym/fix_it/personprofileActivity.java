package com.example.ym.fix_it;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class personprofileActivity extends AppCompatActivity {

    private TextView UserName, FullName, CountryName,phonenumber,gender,bi,showrate;
    private CircleImageView profileimage;
    private FirebaseDatabase database;
    private DatabaseReference useref, UsersReff, mdatabase;
    private FirebaseAuth mauth;
    private String senderuserid, receiveruserid,currentuserid, currentstate,postkey;
    private Button button;
    private RatingBar mrating;
    private EditText feedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personprofile);





        button = (Button) findViewById(R.id.btnSubmit);


        database = FirebaseDatabase.getInstance();
        mauth = FirebaseAuth.getInstance();

        senderuserid = mauth.getCurrentUser().getUid();
        receiveruserid = getIntent().getExtras().get("visit_user_id").toString();
        UsersReff = FirebaseDatabase.getInstance().getReference().child("Users");



      initializefields();



      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent profile = new Intent(personprofileActivity.this, rateandreviewActivity.class);
              profile.putExtra("visit_user_id", receiveruserid);
              startActivity(profile);
          }
      });





        UsersReff.child(receiveruserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String myprofileimage = dataSnapshot.child("profileimage").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String bio = dataSnapshot.child("status").getValue().toString();
                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String mygender = dataSnapshot.child("gender").getValue().toString();
                    String myphonenumber = dataSnapshot.child("phonenum").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();



                    Picasso.with(personprofileActivity.this).load(myprofileimage).placeholder(R.drawable.profile).into(profileimage);


                    UserName.setText("@" +username);
                    FullName.setText(fullname);
                    CountryName.setText(country);
                    phonenumber.setText(myphonenumber);
                    gender.setText(mygender);
                    bi.setText(bio);





                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








    }





    private void initializefields() {
        UserName = (TextView) findViewById(R.id.personusername);
        FullName = (TextView)findViewById(R.id.personfullname);
        CountryName = (TextView) findViewById(R.id.personcountry);
        phonenumber = (TextView) findViewById(R.id.personphone);
        gender = (TextView) findViewById(R.id.persongender);
        profileimage = (CircleImageView) findViewById(R.id.person_profile_pic);
        bi = (TextView) findViewById(R.id.personbio);



    }
}



