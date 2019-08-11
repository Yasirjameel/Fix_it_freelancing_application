package com.example.ym.fix_it;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class peopleActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private EditText searchtext;

    private RecyclerView searchresult;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find People");

      searchresult = (RecyclerView) findViewById(R.id.results);
      searchresult.setHasFixedSize(true);
      searchresult.setLayoutManager(new LinearLayoutManager(this));

      imageButton = (ImageButton) findViewById(R.id.search);
      searchtext = (EditText) findViewById(R.id.searchbox);


      imageButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              String searchboxtext = searchtext.getText().toString();

              searchinput(searchboxtext);
              
          }
      });

    }



    private void searchinput(String searchboxtext) {

        Toast.makeText(peopleActivity.this, "Searching...", Toast.LENGTH_SHORT).show();

        Query searchpeople = userRef.orderByChild("fullname")
                .startAt(searchboxtext).endAt(searchboxtext + "\uf8ff");

        FirebaseRecyclerAdapter<findpeople, findpeopleholder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<findpeople, findpeopleholder>(findpeople.class,
                R.layout.display_layout,
                findpeopleholder.class,
                searchpeople) {


            @Override
            protected void populateViewHolder(findpeopleholder viewHolder, findpeople model, final int position) {

                viewHolder.setFullname(model.getFullname());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setProfileimage(getApplicationContext(),model.getProfileimage());



                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String Visit_user = getRef(position).getKey();

                        Intent profileintent = new Intent(peopleActivity.this, personprofileActivity.class);
                        profileintent.putExtra("visit_user_id", Visit_user);
                        startActivity(profileintent);

                               }
                });
            }
        };
        searchresult.setAdapter(firebaseRecyclerAdapter);
    }

    public static class findpeopleholder extends RecyclerView.ViewHolder
    {
        View mview;

        public findpeopleholder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setProfileimage(Context ctx , String profileimage)
        {
            CircleImageView myimage = (CircleImageView) mview.findViewById(R.id.profileimage);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(myimage);


        }
        public void setFullname(String fullname)
        {
            TextView view = (TextView) mview.findViewById(R.id.userfullname);
            view.setText(fullname);
        }

        public void setStatus (String status)
        {
            TextView mystatus = (TextView) mview.findViewById(R.id.userstatus);
            mystatus.setText(status);
        }
    }

}
