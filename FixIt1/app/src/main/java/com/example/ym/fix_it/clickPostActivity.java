package com.example.ym.fix_it;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class clickPostActivity extends AppCompatActivity {

    private ImageView postimage;
    private TextView postdescription;
    private Button editpost;
    private Button deletepost;
    private DatabaseReference clickpostref;
    private String postkey, currentuserid, databaseuserid, description, image;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Customize");

        mauth=FirebaseAuth.getInstance();
        currentuserid=mauth.getCurrentUser().getUid();


        postkey = getIntent().getExtras().get("postkey").toString();
        clickpostref = FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey);

      postimage = (ImageView) findViewById(R.id.click_image);
      postdescription = (TextView) findViewById(R.id.click_description);
      editpost = (Button) findViewById(R.id.edit_post);
      deletepost = (Button) findViewById(R.id.delete_post);

      deletepost.setVisibility(View.INVISIBLE);
      editpost.setVisibility(View.INVISIBLE);




      clickpostref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

              if(dataSnapshot.exists())
              {

                  description = dataSnapshot.child("description").getValue().toString();
                  image = dataSnapshot.child("postimage").getValue().toString();

                  databaseuserid = dataSnapshot.child("uid").getValue().toString();


                  postdescription.setText(description);
                  Picasso.with(clickPostActivity.this).load(image).into(postimage);

                  if (currentuserid.equals(databaseuserid)) {

                      deletepost.setVisibility(View.VISIBLE);
                      editpost.setVisibility(View.VISIBLE);

                  }

                  editpost.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          
                          editcurrentpost(description);
                      }
                  });
              }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

      deletepost.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              deletecurrentpost();
          }
      });

    }

    private void editcurrentpost(String description) {

        AlertDialog.Builder builder = new AlertDialog.Builder(clickPostActivity.this);
        builder.setTitle("Edit Post");
        final EditText inputfield = new EditText(clickPostActivity.this);
        inputfield.setText(description);
        builder.setView(inputfield);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                clickpostref.child("description").setValue(inputfield.getText().toString());
                Toast.makeText(clickPostActivity.this, "Post Updated", Toast.LENGTH_SHORT).show();


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
    }

    private void deletecurrentpost() {

      clickpostref.removeValue();
      Sendusertomainactivity();
        Toast.makeText(this,"Post has been deleted.", Toast.LENGTH_SHORT).show();

    }
    private void Sendusertomainactivity() {
        Intent mainintent = new Intent(clickPostActivity.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}
