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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private Button Loginbutton;
    private EditText Useremail , Userpassword;
    private ImageView googlesignbutton;
    private TextView Neednewaccountlink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private Boolean emailaddresschecker;
    private DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        getSupportActionBar().hide();


        mAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");

        Neednewaccountlink=(TextView) findViewById(R.id.Register_account_link);
        Useremail=(EditText) findViewById(R.id.login_email);
        Userpassword=(EditText) findViewById(R.id.login_password);
        Loginbutton=(Button)findViewById(R.id.login_button);
        loadingbar = new ProgressDialog(this);

        Neednewaccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendusertoRegisterActivity();
            }
        });

        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Allowingusertologin();
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

    private void Allowingusertologin() {
        String email = Useremail.getText().toString();
        String password = Userpassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please write your email", Toast.LENGTH_SHORT).show();

        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please write your password", Toast.LENGTH_SHORT).show();

        }
        else
        {
            loadingbar.setTitle("Login");
            loadingbar.setMessage("Please wait,While we are allowing to login into your Account");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(email , password)

                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                verifyemailaddress();
                                loadingbar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error Occured:"+ message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
        }
    }

    private void verifyemailaddress()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        if (user != null && user.isEmailVerified()) {
            if(uid == null || uid.isEmpty()){
                Sendusertosetupactivity();
            }else {
                Sendusertomainactivity();
            }
        }
        else{
            Toast.makeText(LoginActivity.this,"Please verify your account first", Toast.LENGTH_SHORT).show();
            mAuth.signOut();

        }
    }

    private void Sendusertosetupactivity() {
        Intent setupintent = new Intent(LoginActivity.this, SetupActivity.class);
        setupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupintent);
        finish();
    }
    private void Sendusertomainactivity() {
        Intent mainintent = new Intent(LoginActivity.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
    private void sendusertoRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);

    }
}
