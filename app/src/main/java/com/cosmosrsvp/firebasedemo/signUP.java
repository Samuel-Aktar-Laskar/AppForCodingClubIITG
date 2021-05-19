package com.cosmosrsvp.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signUP extends AppCompatActivity {
    private EditText username, password1,password2;
    private Button signup, Goback;
    private ProgressBar loading;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_u_p);
        username=findViewById(R.id.usernameR);
        password1=findViewById(R.id.passR);
        password2=findViewById(R.id.passRC);

        loading=findViewById(R.id.progressR);
        signup=findViewById(R.id.signUp);
        Goback=findViewById(R.id.loginback);
        auth=FirebaseAuth.getInstance();


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=password1.getText().toString().trim();
                String passC=password2.getText().toString().trim();


                String Email=username.getText().toString().trim();

                if(!pass.equals(passC) )
                {
                    Toast.makeText(getApplicationContext(), "Password not matching", Toast.LENGTH_SHORT).show();
                    password1.setError("Password not matching");
                    password2.setError("Password not matching");
                    password1.requestFocus();
                    return;
                }


                if(Email.isEmpty())
                {

                    username.setError("Email is required");
                    username.requestFocus();
                    return;

                }
                if (pass.isEmpty())
                {
                    password1.setError("Password can't be empty");
                    password1.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    username.setError("Pleas Enter a valid Email");
                    username.requestFocus();
                    return;

                }
                loading.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loading.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Successfully registereed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),ProfileUpdate.class));
                        }
                        else Toast.makeText(signUP.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }});

        Goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        }


}
