package com.example.administrator.friendschat;


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
import android.support.v7.widget.Toolbar;

import com.example.administrator.friendschat.Login;
import com.example.administrator.friendschat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class ForgetActivity extends AppCompatActivity {

    private EditText Email;
    private Button send_button;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser user = mAuth.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.forget_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");
        
        send_button = findViewById(R.id.send_email);
        Email = findViewById(R.id.email);


        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ForgetActivity.this, "Password has been changed..", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgetActivity.this, Login.class);
                startActivity(intent);

            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToChangePassword();
            }
        });

    }

    private void AllowUserToChangePassword() {
        String userEmail = Email.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Please write your valid email address first....", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ForgetActivity.this, "Please check your Email Account, If you want to reset your password...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgetActivity.this,Login.class));
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(ForgetActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
