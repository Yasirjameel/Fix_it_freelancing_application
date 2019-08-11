package com.example.ym.fix_it;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class commentsActivity extends AppCompatActivity {


    private ImageButton postcommentbutton;
    private EditText commentinputtext;
    private RecyclerView commentslist;
    private String postkey1, currentid;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        postkey1 = getIntent().getExtras().get("postkey").toString();

        mauth = FirebaseAuth.getInstance();

        currentid = mauth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey1).child("Comments");



     commentslist = (RecyclerView) findViewById(R.id.commentlist);
     commentslist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentslist.setLayoutManager(linearLayoutManager);


        commentinputtext = (EditText) findViewById(R.id.comment);
        postcommentbutton = (ImageButton) findViewById(R.id.postcomment);


          postcommentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             UsersRef.child(currentid).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {

                     if(dataSnapshot.exists())
                     {
                         String fullName = dataSnapshot.child("fullname").getValue().toString();





                         validatecomment(fullName);


                         commentinputtext.setText("");
                     }








                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });
            }
        });



    }



    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<Comments, commentsviewholder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, commentsviewholder>
                (
                        Comments.class,
                        R.layout.allcommentlayout,
                        commentsviewholder.class,
                        PostsRef


                ) {
            @Override
            protected void populateViewHolder(final commentsviewholder viewHolder, Comments model,final int position) {


                viewHolder.setProfileimage(getApplicationContext(), model.profileimage);
                viewHolder.setFullname(model.getFullname());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());



            }
        };

        commentslist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class commentsviewholder extends RecyclerView.ViewHolder
    {
        View mview;

        public commentsviewholder(@NonNull View itemView) {
            super(itemView);

        mview= itemView;


        }

        public void setProfileimage(Context ctx , String profileimage)
        {
            CircleImageView myimage = (CircleImageView) mview.findViewById(R.id.comment_profile_image);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(myimage);


        }

        public void setFullname(String fullname)
        {
            TextView myfullname = (TextView) mview.findViewById(R.id.comment_username);
            myfullname.setText(fullname+" ");
        }

        public void setComment(String comment)
        {
           TextView mycomment = (TextView) mview.findViewById(R.id.comment_text);
            mycomment.setText(comment);
        }
        public void setDate(String date)
        {
            TextView mydate = (TextView) mview.findViewById(R.id.comment_date);
            mydate.setText(" Date "+date);
        }
        public void setTime(String time)
        {
           TextView mytime = (TextView) mview.findViewById(R.id.comment_time);
           mytime.setText(" Time "+time);
        }
    }




    private void validatecomment(final String fullName) {

    final  String commenttext = commentinputtext.getText().toString();

      if(TextUtils.isEmpty(commenttext))
      {
          Toast.makeText(this,"", Toast.LENGTH_SHORT).cancel();


      }
      else
      {
          Calendar calFordDate = Calendar.getInstance();
          SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
          final String saveCurrentDate = currentDate.format(calFordDate.getTime());

          Calendar calFordTime = Calendar.getInstance();
          SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String saveCurrentTime = currentTime.format(calFordDate.getTime());

        final String randomkey = currentid + saveCurrentDate + saveCurrentTime;

        UsersRef.child(currentid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String profileimage = dataSnapshot.child("profileimage").getValue().toString();
                final String fullname = dataSnapshot.child("fullname").getValue().toString();



                HashMap commentmap =  new HashMap();
                commentmap.put("uid", currentid);
                commentmap.put("comment", commenttext);
                commentmap.put("date", saveCurrentDate);
                commentmap.put("time", saveCurrentTime);
                commentmap.put("fullname", fullname);
                commentmap.put("profileimage", profileimage);
                PostsRef.child(randomkey).updateChildren(commentmap)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if(task.isSuccessful())
                                {
                                    Toast.makeText(commentsActivity.this,"You have commented sucessfully", Toast.LENGTH_SHORT).cancel();
                                }
                                else {

                                    Toast.makeText(commentsActivity.this,"Error Occured... Try again", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



      }

    }
}
