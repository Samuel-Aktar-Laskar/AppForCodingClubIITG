package com.cosmosrsvp.firebasedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //my edits
    private static final int CHOOSE_IMAGE = 101;
    String Email;
    private ProgressBar loading;
    private  FirebaseDatabase database;
    private ImageView profilePic;
    private  FirebaseAuth mAuth;
    private String photoUri;
    private GoogleSignInClient mGoogleSignInClient;

    private  Button editprofile, logout;
    private TextView fullName, contactNo, profession, country;
    Artist obj;
    StorageReference s;




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public profileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profileFragment newInstance(String param1, String param2) {
        profileFragment fragment = new profileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }





    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        contactNo=view.findViewById(R.id.contactNo);
        profilePic = (ImageView)view.findViewById(R.id.profile);
        editprofile=(Button)view.findViewById(R.id.editProfile);
        logout=view.findViewById(R.id.logout);
        fullName=view.findViewById(R.id.fullName);
        mAuth=FirebaseAuth.getInstance();
        loading=view.findViewById(R.id.loading);

        profession=view.findViewById(R.id.profession);
        country=view.findViewById(R.id.nation);

        Toast.makeText(getActivity(), "Inside oncreateview", Toast.LENGTH_SHORT).show();
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),ProfileUpdate.class);
                intent.putExtra("NameOfUser",obj.fullname.toString());
                intent.putExtra("ContactNo",obj.contactno.toString());
                intent.putExtra("Profession",obj.profession.toString());
                intent.putExtra("Country",obj.country.toString());
                intent.putExtra("path",obj.path);

                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(getActivity(),gso);
                if (googleSignInClient !=null) {
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FirebaseAuth.getInstance().signOut(); // very important if you are using firebase.
                                Intent login_intent = new Intent(getActivity(), MainActivity.class);
                                login_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // clear previous task (optional)
                                login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(login_intent);
                                getActivity().finish();

                            }
                        }
                    });
                }
                else {
                Intent intent= new Intent(getActivity(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();}



            }
        });

        //setting dp
        loading.setVisibility(View.VISIBLE);
        FirebaseUser user =mAuth.getCurrentUser();







        if(user != null)
        {
             Email=user.getEmail();
        }
        Query query = FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("email")
                .equalTo(Email);
        query.addListenerForSingleValueEvent(valueEventListener);







        // Inflate the layout for this fragment
        return view;

    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            try {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        obj = snapshot.getValue(Artist.class);
                        //Toast.makeText(getActivity(), obj.path, Toast.LENGTH_SHORT).show();

                    }
                    fullName.setText("Full Name: "+obj.fullname);
                    contactNo.setText("Contact No: "+obj.contactno);
                    country.setText("Country: "+obj.country);
                    profession.setText("Profession: "+obj.profession);
                    //retrieving photo
                    try {

                        s=FirebaseStorage.getInstance().getReference().child("profilepics/"+obj.path+".jpg");


                        final File localfile=File.createTempFile("sam",".jpg");
                        s.getFile(localfile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(getActivity(), "Picture retrieved", Toast.LENGTH_SHORT).show();
                                        Bitmap  bitmap= BitmapFactory.decodeFile(localfile.getAbsolutePath());

                                        profilePic.setImageBitmap(bitmap);
                                        loading.setVisibility(View.GONE);


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch(Exception e)
            {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


}
