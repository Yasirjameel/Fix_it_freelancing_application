package com.example.ym.fix_it;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatActivity extends AppCompatActivity implements LocationListener {

    private Button getLocationBtn;
    private TextView locationtext,locations;
    LocationManager locationManager;
    private ImageButton sendmessage;
    private EditText usermessageinput;
    private RecyclerView usermessageslist;
    private String messagereceiverid, messagereceivername,messagesenderid,saveCurrentDate,saveCurrentTime;
    private TextView receiverusername, userlastseen;
    private CircleImageView receiverprofileimage;
    private DatabaseReference rootref, Usersref, notificationref;
    private FirebaseAuth mauth;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private Messagesadapter messagesadapter;
    //private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mauth = FirebaseAuth.getInstance();
        messagesenderid = mauth.getCurrentUser().getUid();

        getLocationBtn = (Button)findViewById(R.id.getLocationBtn);
        locationtext = (TextView)findViewById(R.id.locationText);
        locations = (TextView) findViewById(R.id.locationText1);

       /** String text = "Click here to see user location";

        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                getLocation();
            }
        };

        ss.setSpan(clickableSpan, 7, 11 ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        locations.setText(ss);
        locations.setMovementMethod(LinkMovementMethod.getInstance()); **/

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }



       /** getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 usermessageinput.setText(locations.getText());

            }
        });**/

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();

            }
        });


        rootref = FirebaseDatabase.getInstance().getReference();
        Usersref = FirebaseDatabase.getInstance().getReference().child("Users");
        notificationref = FirebaseDatabase.getInstance().getReference().child("Notifications");

        messagereceiverid = getIntent().getExtras().get("visit_user_id").toString();
       messagereceivername = getIntent().getExtras().get("username").toString();
    Initializedfields();

    displayuserinfo();

    sendmessage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Sendmessage();
        }
    });

    Fetchmessages();




    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onLocationChanged(Location location) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ location.getLatitude() + "," + location.getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

       // locationtext.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            usermessageinput.setText(locations.getText() + "\n"+addresses.get(0).getAddressLine(0));
        }catch(Exception e)
        {

        }

    }


    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(chatActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }





    private void Fetchmessages()
    {
        rootref.child("Messages").child(messagesenderid).child(messagereceiverid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if(dataSnapshot.exists())
                        {

                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messagesadapter.notifyDataSetChanged();
                            usermessageslist.smoothScrollToPosition(usermessageslist.getAdapter ().getItemCount ()-1);

                          //counter = messagesList.size();

                        }
                       // String count = String.valueOf(counter);
                        //usermessageinput.setText(count);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void Sendmessage() {

        updateuserstatus("online");

        final String messagetext = usermessageinput.getText().toString();


        if(TextUtils.isEmpty(messagetext))
        {
            Toast.makeText(this,"Please type a message first", Toast.LENGTH_SHORT).show();
        }
        else
        {
           String message_sender_ref = "Messages/" + messagesenderid + "/" + messagereceiverid;
            String message_receiver_ref = "Messages/" + messagereceiverid + "/" + messagesenderid;

            DatabaseReference user_message_key= rootref.child("Messages").child(messagesenderid).child(messagereceiverid).push();
            String message_push_id = user_message_key.getKey();


            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm ss");
            saveCurrentTime = currentTime.format(calFordTime.getTime());

            Map messagetextbody = new HashMap<>();
            messagetextbody.put("message", messagetext);
            messagetextbody.put("time", saveCurrentTime);
            messagetextbody.put("date", saveCurrentDate);
            messagetextbody.put("type", "text");
            messagetextbody.put("from", messagesenderid);


            Map messagebodydetails = new HashMap();
            messagebodydetails.put(message_sender_ref + "/" + message_push_id , messagetextbody);
            messagebodydetails.put(message_receiver_ref + "/" + message_push_id , messagetextbody);

            rootref.updateChildren(messagebodydetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful())
                    {

                        HashMap<String, String> messagenotification = new HashMap<>();
                        messagenotification.put("from", messagesenderid);
                        messagenotification.put("type",messagetext);
                        notificationref.child(messagereceiverid).push()
                                .setValue(messagenotification)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(chatActivity.this,"Message sent", Toast.LENGTH_SHORT).cancel();
                                            usermessageinput.setText("");
                                        }
                                    }
                                });





                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(chatActivity.this, " Error" + message, Toast.LENGTH_SHORT).show();
                        usermessageinput.setText("");
                    }




                }
            });



        }



    }

   /** public void sendNotification(View view) {

        String messagetext = usermessageinput.getText().toString();
        //Get an instance of NotificationManager//

       NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Channel_Id);
                        builder.setSmallIcon(R.drawable.ic_message_black_24dp);
                        builder.setContentTitle("hello");
                        builder.setContentText("hi");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(Notification_id, builder.build());


    }**/
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

        Usersref.child(messagesenderid).child("usersstate").updateChildren(currentstatemap);
    }

    private void displayuserinfo() {


        receiverusername.setText(messagereceivername);

    rootref.child("Users").child(messagereceiverid).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(dataSnapshot.exists())
            {
                final String profileimg = dataSnapshot.child("profileimage").getValue().toString();
                final String type = dataSnapshot.child("usersstate").child("type").getValue().toString();
                final String lastdate = dataSnapshot.child("usersstate").child("date").getValue().toString();
                final String lasttime = dataSnapshot.child("usersstate").child("time").getValue().toString();

                if(type.equals("online"))
                {
                    userlastseen.setText("online");
                }
                else
                {
                    userlastseen.setText("Last Seen: " + lasttime + " " + lastdate);
                }




                Picasso.with(chatActivity.this).load(profileimg).placeholder(R.drawable.profile).into(receiverprofileimage);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
    }

    private void Initializedfields() {






    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowCustomEnabled(true);
    LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View action_bar = layoutInflater.inflate(R.layout.chat_custom_bar, null);
    actionBar.setCustomView(action_bar);


        sendmessage = (ImageButton) findViewById(R.id.send_message);
        usermessageinput = (EditText)findViewById(R.id.input_message);
        receiverusername = (TextView) findViewById(R.id.custom_profile_name);
        receiverprofileimage = (CircleImageView) findViewById(R.id.custom_profile_image);
        userlastseen = (TextView)findViewById(R.id.custom_user_last_seen);

        messagesadapter = new Messagesadapter(messagesList);
        usermessageslist = (RecyclerView) findViewById(R.id.message_list_users);
        linearLayoutManager = new LinearLayoutManager(this);
        usermessageslist.setHasFixedSize(true);
        usermessageslist.setLayoutManager(linearLayoutManager);
        usermessageslist.setAdapter(messagesadapter);

    }
}
