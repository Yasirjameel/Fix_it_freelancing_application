package com.example.ym.fix_it;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class rateandreviewActivity extends AppCompatActivity {
    private RatingBar mrating;
    private EditText feedback;
    private FirebaseDatabase database;
    private Button button;
    private TextView showrate,showrate1,showrate2;
    private FirebaseAuth mauth;
    private DatabaseReference mdatabase,UsersReff;
    private String senderuserid,currentuid,postrandomname,receiveruserid;
    private long countreviews ;
    private RelativeLayout mylayout;
    private ListView simpleList,simplelist2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rateandreview);
        mylayout = (RelativeLayout) findViewById(R.id.rating_layout);


        database = FirebaseDatabase.getInstance();
        mauth = FirebaseAuth.getInstance();

        senderuserid = mauth.getCurrentUser().getUid();


        simplelist2=(ListView)findViewById(R.id.simple_list_view2);
        simpleList=(ListView)findViewById(R.id.simple_list_view);
        mrating =(RatingBar) findViewById(R.id.ratingBar);
        button = (Button) findViewById(R.id.btnSubmit);
        feedback = (EditText) findViewById(R.id.feed);
        showrate = (TextView) findViewById(R.id.rate);

        mdatabase=FirebaseDatabase.getInstance().getReference().child("Ranking");




        rate();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = mrating.getRating();
                String review = feedback.getText().toString();
                String sender_id = mauth.getCurrentUser().getUid();
                String reciever_id = getIntent().getExtras().get("visit_user_id").toString();


                Map savedata = new HashMap();
                savedata.put("rating", rating);
                savedata.put("review", review);
                savedata.put("sender_id", sender_id);
                savedata.put("reciever_id",reciever_id);
                mdatabase.push().setValue(savedata);
                feedback.setText("");
            }

        });






        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    ArrayList<String> reviews  = new ArrayList<>();
                    ArrayList<String> rating  = new ArrayList<>();
                    reviews.add("REVIEWS");
                    rating.add("RATINGS");
                    String current_profile_id = getIntent().getExtras().get("visit_user_id").toString();
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        String reciever_id = ds.child("reciever_id").getValue().toString();
                        if (reciever_id.equals(current_profile_id)){
                            reviews.add(ds.child("review").getValue().toString());
                            rating.add(ds.child("rating").getValue().toString());

                        }
                    }

                    ArrayAdapter<String> ArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item, R.id.list_view_item, reviews);
                    simpleList.setAdapter(ArrayAdapter);
                    ArrayAdapter<String> ArrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item2, R.id.list_view_item2, rating);
                    simplelist2.setAdapter(ArrayAdapter2);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void rate() {
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                countreviews = dataSnapshot.getChildrenCount();
            }
            else {
                countreviews = 0;
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




}
