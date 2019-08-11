package com.example.ym.fix_it;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private EditText Useremail, Userpassword, Conformpassword;
    private Button createaccount;
    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;
    private DatabaseReference rootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        mAuth=FirebaseAuth.getInstance();
        rootref = FirebaseDatabase.getInstance().getReference();

        Useremail = (EditText) findViewById(R.id.register_email);
        Userpassword = (EditText) findViewById(R.id.register_password);
        Conformpassword = (EditText) findViewById(R.id.register_conform_passwprd);
        createaccount = (Button) findViewById(R.id.register_create_account);
        loadingbar = new ProgressDialog(this);

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();

        if(CurrentUser != null)
        {
            Sendusertomainactivity();
        }
    }

    private void Sendusertomainactivity() {
        Intent mainintent = new Intent(RegisterActivity.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }

    private void CreateNewAccount() {
        String email = Useremail.getText().toString();
        String password = Userpassword.getText().toString();
        String conformpassword = Conformpassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write your email", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(conformpassword))
        {
            Toast.makeText(this, "Please write your conform password", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(conformpassword))
        {
            Toast.makeText(this, "Your password do not match", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Creating New Account");
            loadingbar.setMessage("Please wait,While we are creating your new Account");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email, password)

                   .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful())
                           {

                             /*  String devicetoken = FirebaseInstanceId.getInstance().getToken();

                               String currentuserid = mAuth.getCurrentUser().getUid();
                               rootref.child("Users").child(currentuserid).setValue("");

                               rootref.child("Users").child(currentuserid).child("device_token")
                                       .setValue(devicetoken);*/


                               sendemailverifcation();
                               Toast.makeText(RegisterActivity.this,"You are authenticated sucessfully", Toast.LENGTH_SHORT).show();
                               loadingbar.dismiss();
                           }
                           else {
                               String message = task.getException().getMessage();
                               Toast.makeText(RegisterActivity.this,"Error Occured"+ message, Toast.LENGTH_SHORT).show();
                               loadingbar.dismiss();
                           }
                       }
                   });

        }
    }

    private void sendemailverifcation()
    {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful())
               {
                   Toast.makeText(RegisterActivity.this, " Registration sucessfull, we have sent you an email, please check your account", Toast.LENGTH_LONG).show();

                   SendUsertologinActivity();
                   mAuth.signOut();
               }
               else
               {
                   String error = task.getException().getMessage();
                   Toast.makeText(RegisterActivity.this, "Error occured" + error , Toast.LENGTH_LONG).show();
                   mAuth.signOut();
               }
                }
            });
        }
    }


    private void SendUsertologinActivity() {
        Intent loginintent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }
}
