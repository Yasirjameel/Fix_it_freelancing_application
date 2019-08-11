package com.example.ym.fix_it;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileActivity extends AppCompatActivity {

        private TextView UserName, FullName, CountryName,phonenumber,gender,bi;
        private CircleImageView profileimage;
        private DatabaseReference useref, postref;
        private FirebaseAuth mauth;


        private String currentuserid;
private int countposts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Profile");



        mauth = FirebaseAuth.getInstance();
        currentuserid = mauth.getCurrentUser().getUid();
        useref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserid);
        postref = FirebaseDatabase.getInstance().getReference().child("Posts");



      UserName = (TextView) findViewById(R.id.username);
      FullName = (TextView)findViewById(R.id.profile_name);
      CountryName = (TextView) findViewById(R.id.country);
      phonenumber = (TextView) findViewById(R.id.phone);
      gender = (TextView) findViewById(R.id.gender);
      profileimage = (CircleImageView) findViewById(R.id.profile_pic);
      bi = (TextView) findViewById(R.id.bio);







       useref.addValueEventListener(new ValueEventListener() {
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

                   Picasso.with(profileActivity.this).load(myprofileimage).placeholder(R.drawable.profile).into(profileimage);


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








}
