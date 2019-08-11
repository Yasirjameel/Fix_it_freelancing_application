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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class allpeopleActivity extends AppCompatActivity {

    private RecyclerView myfriendlist;
    private DatabaseReference allpeople,Userref;
    private FirebaseAuth mauth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allpeople);


        mauth =FirebaseAuth.getInstance();
       online_user_id = mauth.getCurrentUser().getUid();
//        allpeople = FirebaseDatabase.getInstance().getReference().child("following").child(online_user_id);
        Userref = FirebaseDatabase.getInstance().getReference().child("Users");

    myfriendlist = (RecyclerView) findViewById(R.id.peoplelist);
        myfriendlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myfriendlist.setLayoutManager(linearLayoutManager);


        displayallpeople();
    }


   public void updateuserstatus(String state)
    {
        String savecurrentdate, savecurrenttime;

        Calendar calfordate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd, yyyy");
        savecurrentdate = currentdate.format(calfordate.getTime());


        Calendar calfortime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
        savecurrenttime = currenttime.format(calfortime.getTime());

        Map currentstatemap = new HashMap();
        currentstatemap.put("time", savecurrenttime);
        currentstatemap.put("date", savecurrentdate);
        currentstatemap.put("type", state);

        Userref.child(online_user_id).child("usersstate").updateChildren(currentstatemap);
    }


    @Override
    protected void onStart() {
        super.onStart();


        updateuserstatus("online");
    }

    @Override
    protected void onStop() {
        super.onStop();

        updateuserstatus("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        updateuserstatus("offline");
    }




    private void displayallpeople() {

        FirebaseRecyclerAdapter<allpeople,allpeopleViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<allpeople, allpeopleViewHolder>(
                        allpeople.class,
                R.layout.display_layout,
                allpeopleViewHolder.class,
                Userref


        ) {
            @Override
            protected void populateViewHolder(final allpeopleViewHolder viewHolder, allpeople model, int position) {


                viewHolder.setStatus(model.getStatus());

                final String usersid = getRef(position).getKey();

                Userref.child(usersid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            final String username = dataSnapshot.child("fullname").getValue().toString();
                            final String profileimg = dataSnapshot.child("profileimage").getValue().toString();

                            final String type;

                            if(dataSnapshot.hasChild("usersstate"))
                            {
                                type = dataSnapshot.child("usersstate").child("type").getValue().toString();

                                if(type.equals("online"))
                                {
                                    viewHolder.onlinestatusview.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    viewHolder.onlinestatusview.setVisibility(View.INVISIBLE);
                                }
                            }

                            viewHolder.setfullname(username);
                            viewHolder.setProfileimage(getApplicationContext(), profileimg);

                         viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {

                                 CharSequence options[] = new CharSequence[]
                                         {
                                           username + "'s Profile",
                                                 "Send message"
                                         };
                                 AlertDialog.Builder builder = new AlertDialog.Builder(allpeopleActivity.this);
                                 builder.setTitle("Select Options");

                                 builder.setItems(options, new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialogInterface, int i) {

                                         if(i == 0)
                                         {

                                             Intent profile = new Intent(allpeopleActivity.this, personprofileActivity.class);
                                             profile.putExtra("visit_user_id", usersid);
                                             startActivity(profile);
                                         }
                                         if(i == 1)
                                         {
                                             Intent chat = new Intent(allpeopleActivity.this, chatActivity.class);
                                             chat.putExtra("visit_user_id", usersid);
                                             chat.putExtra("username", username);
                                             startActivity(chat);
                                         }
                                     }
                                 });
                                 builder.show();
                             }
                         });
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        myfriendlist.setAdapter(firebaseRecyclerAdapter);
    }


    public static class allpeopleViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        ImageView onlinestatusview;

        public allpeopleViewHolder(@NonNull View itemView) {
            super(itemView);

        mview = itemView;

        onlinestatusview = (ImageView) itemView.findViewById(R.id.all_user_online_icon);
        }
        public void setProfileimage(Context ctx , String profileimage)
        {
            CircleImageView myimage = (CircleImageView) mview.findViewById(R.id.profileimage);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(myimage);


        }
        public void setfullname(String fullname)
        {
            TextView view = (TextView) mview.findViewById(R.id.userfullname);
            view.setText(fullname);
        }

        public void setStatus(String status)
        {
            TextView mystatus = (TextView) mview.findViewById(R.id.userstatus);
            mystatus.setText(status);
        }
    }
}
