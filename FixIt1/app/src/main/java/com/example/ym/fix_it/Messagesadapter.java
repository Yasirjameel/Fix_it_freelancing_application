package com.example.ym.fix_it;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messagesadapter extends RecyclerView.Adapter<Messagesadapter.messageviewholder> {

    private List<Messages> usersmessageslist;
    private FirebaseAuth mauth;
    private DatabaseReference usersdatabaseref;

    public Messagesadapter (List<Messages> usersmessageslist)
    {
        this.usersmessageslist = usersmessageslist;
    }

    public class messageviewholder extends RecyclerView.ViewHolder
    {
        public TextView sendermessagetext,receivermessagetext;
        public CircleImageView receiverprofileimage;


        public messageviewholder(@NonNull View itemView) {
            super(itemView);

            sendermessagetext = (TextView) itemView.findViewById(R.id.sender_message_text);
            receivermessagetext = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverprofileimage = ( CircleImageView) itemView.findViewById(R.id.message_profile_image);
        }
    }

    @NonNull
    @Override
    public messageviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_layout_user, viewGroup , false );

        mauth = FirebaseAuth.getInstance();

        return new messageviewholder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull final messageviewholder messageviewholder, int i) {

        String messagesenderid = mauth.getCurrentUser().getUid();
        Messages messages = usersmessageslist.get(i);

        String fromUserid = messages.getFrom();
        String frommessagetype = messages.getType();

        usersdatabaseref = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserid);
        usersdatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    String image  = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.with(messageviewholder.receiverprofileimage.getContext()).load(image).placeholder(R.drawable.profile).into(messageviewholder.receiverprofileimage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(frommessagetype.equals("text"))
        {
            messageviewholder.receivermessagetext.setVisibility(View.INVISIBLE);
            messageviewholder.receiverprofileimage.setVisibility(View.INVISIBLE);

            if(fromUserid.equals(messagesenderid))
            {
                messageviewholder.sendermessagetext.setBackgroundResource(R.drawable.sender_messagetextbackground);
                messageviewholder.sendermessagetext.setTextColor(Color.WHITE);
                messageviewholder.sendermessagetext.setGravity(Gravity.LEFT);
                messageviewholder.sendermessagetext.setText(messages.getMessage());
            }
            else
            {
                messageviewholder.sendermessagetext.setVisibility(View.INVISIBLE);

                messageviewholder.receivermessagetext.setVisibility(View.VISIBLE);
                messageviewholder.receiverprofileimage.setVisibility(View.VISIBLE);

                messageviewholder.receivermessagetext.setBackgroundResource(R.drawable.receiver_text_background);
                messageviewholder.receivermessagetext.setTextColor(Color.WHITE);
                messageviewholder.receivermessagetext.setGravity(Gravity.LEFT);
                messageviewholder.receivermessagetext.setText(messages.getMessage());
            }




        }
    }



    @Override
    public int getItemCount(){
        return usersmessageslist.size();
    }
}
