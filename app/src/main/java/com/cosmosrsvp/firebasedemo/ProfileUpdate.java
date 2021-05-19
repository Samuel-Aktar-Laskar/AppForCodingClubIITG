package com.cosmosrsvp.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class ProfileUpdate extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    ImageView profilePic;
    Button Edit;
    ImageButton submit;
    EditText fullName, contactNo, profession, country;
    Uri uriProfileImage;
    String profileImageUrl;
    FirebaseAuth mauth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String path;
    String path2;
    StorageReference s;
    private String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        //inatialising
        profilePic = findViewById(R.id.profile);
        Edit=findViewById(R.id.edit);
        submit=findViewById(R.id.submit);
        fullName=findViewById(R.id.fullname);
        contactNo=findViewById(R.id.phoneno);
        profession=findViewById(R.id.profession);
        country=findViewById(R.id.country);
        mauth=FirebaseAuth.getInstance();

        //checking if user is registered already

        //Fetching current values
        try {
            fullName.setText(getIntent().getExtras().getString("NameOfUser"));
            contactNo.setText(getIntent().getExtras().getString("ContactNo"));
            profession.setText(getIntent().getExtras().getString("Profession"));
            country.setText(getIntent().getExtras().getString("Country"));
            path2=getIntent().getExtras().getString("path");
            if(path2 != null)
            {
                //retrieving photo
                try {

                    s=FirebaseStorage.getInstance().getReference().child("profilepics/"+path2+".jpg");


                    final File localfile=File.createTempFile("sam",".jpg");
                    s.getFile(localfile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "Picture retrieved", Toast.LENGTH_SHORT).show();
                                    Bitmap  bitmap= BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                    profilePic.setImageBitmap(bitmap);



                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChoser();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

    }
    //methods to choose image
    private void showImageChoser() {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select profile image"),CHOOSE_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CHOOSE_IMAGE && resultCode==RESULT_OK && data != null && data.getData()!=null)
        {
            uriProfileImage=data.getData();
            try
            {
                Bitmap bitmap= MediaStore.Images.Media
                        .getBitmap(getContentResolver(),uriProfileImage);
                profilePic.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //methods to upload image to firebase server

    private void uploadImageToFirebaseStorage()
    {   path=String.valueOf(System.currentTimeMillis());
        StorageReference pofileImageRef= FirebaseStorage.getInstance()
                .getReference("profilepics/"+path+".jpg");
        if(uriProfileImage !=null)
        {
            pofileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileImageUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"failed"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    //method to save uder info

    private void saveUserInformation()
    {
        String fullName_text=fullName.getText().toString();
        String contactNo_text=contactNo.getText().toString();
        String profession_text=profession.getText().toString();
        String country_text=country.getText().toString();


        if(fullName_text.isEmpty())
        {
            fullName.setError("Required field");
            fullName.requestFocus();
            return;

        }
        if (contactNo_text.isEmpty())
        {
            contactNo.setError("Required field");
            contactNo.requestFocus();
            return;
        }
        if(profession_text.isEmpty())
        {
            profession.setError("Required field");
            profession.requestFocus();
            return;

        }
        if(country_text.isEmpty())
        {
            country.setError("Required field");
            country.requestFocus();
            return;
        }
        FirebaseUser u=mauth.getCurrentUser();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){

            Email=(signInAccount.getEmail());
        }
        else
            Email=u.getEmail();
        try {
            database = FirebaseDatabase.getInstance();
            Artist sam=new Artist(contactNo_text,country_text,fullName_text,profession_text,Email,path);
            myRef = database.getReference("users");
            myRef.child(contactNo_text).setValue(sam);
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),Home.class));
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Failed"+e.getMessage()  , Toast.LENGTH_SHORT).show();
        }


        FirebaseUser user= mauth.getCurrentUser();
        if(user != null)

        {
            UserProfileChangeRequest profile=new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName_text)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "successfully  saved", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }



}


