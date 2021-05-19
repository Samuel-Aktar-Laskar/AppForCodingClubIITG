package com.cosmosrsvp.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.ApiExceptionUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 11;
    EditText email, password;
    ProgressBar prog;
    Button Login, SignUp;
    private FirebaseAuth mAuth;
    private Spinner loginMethod;
    private ProgressBar loading;
    private  Artist obj;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.username);
        password = findViewById(R.id.pass);
        Login = findViewById(R.id.login);
        prog = findViewById(R.id.progress);
        mAuth = FirebaseAuth.getInstance();
        SignUp = findViewById(R.id.signUp);
        loginMethod = findViewById(R.id.loginMethodId);
        loading = findViewById(R.id.loading);
        obj=new Artist();



        ArrayList<String> loginMethodsArray = new ArrayList<>();
        loginMethodsArray.add("Login with Email and password");
        loginMethodsArray.add("Login with Google Account");
        loginMethodsArray.add("Login with Microsoft Account");


        ArrayAdapter loginAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, loginMethodsArray);
        loginMethod.setAdapter(loginAdapter);

        //Setting on click listener
        loginMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1: {
                        Toast.makeText(MainActivity.this, "google selected", Toast.LENGTH_SHORT).show();
                        createRequest();
                        loading.setVisibility(View.VISIBLE);
                        signIn();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), signUP.class));
                finish();
            }
        });


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete this line of code

                String pass = password.getText().toString().trim();

                String Email = email.getText().toString().trim();


                if (Email.isEmpty()) {

                    email.setError("Email is required");
                    email.requestFocus();
                    return;

                }
                if (pass.isEmpty()) {
                    password.setError("Password can't be empty");
                    password.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    email.setError("Pleas Enter a valid Email");
                    email.requestFocus();
                    return;

                }
                prog.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        prog.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            Toast.makeText(this, "Undesired thing happeded", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Home.class));

        }
    }

    private void createRequest() {


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        loading.setVisibility(View.GONE);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, "Exception A " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Throwable throwable) {
                Toast.makeText(this, "ExceptionA1 " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //Checking if user is new or old

                                // This is an existing user, show them a welcome back screen.
                                Intent intent = new Intent(getApplicationContext(), ProfileUpdate.class);
                                finish();
                                startActivity(intent);




                            Toast.makeText(MainActivity.this, "Reached the destiney", Toast.LENGTH_SHORT).show();

                            // Sign in success, update UI with the signed-in user's information

                            //Checking if user already had an acc







                        } else {
                            Toast.makeText(MainActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();


                        }


                        // ...
                    }
                });


    }


}
