package com.principal.dallamada.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.principal.dallamada.LoginActivity;
import com.principal.dallamada.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ImageView imgProfile;
    private TextView textViewUsername, textViewEmail, textViewChangeImage;
    private Switch switchBtn;
    private Button btn_logOut;
    private static final int PICK_PHOTO_FOR_AVATAR = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate( R.layout.fragment_profile, container, false );

        imgProfile = v.findViewById( R.id.imageProfile );
        textViewUsername = v.findViewById( R.id.textViewUsernameProfile );
        textViewEmail = v.findViewById( R.id.textViewEmailProfile );
        switchBtn = v.findViewById( R.id.switchDarkMode );
        btn_logOut = v.findViewById( R.id.btn_log_out );
        textViewChangeImage = v.findViewById( R.id.changeImageProfile );


        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection( "users" )
                .document( user.getEmail() )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot document = task.getResult();
                        textViewUsername.setText( "Nombre de usuario " + document.getString( "username" ) );
                        textViewEmail.setText( "Email " + document.getString( "email" ) );
                        Glide.with(ProfileFragment.this).load(document.getString("imgProfile")).into(imgProfile);
                    }
                } );
        textViewChangeImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickImage();
            }
        } );

        checkingDarkMode();
        switchBtn.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES );
                } else {
                    AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO );
                }
            }
        } );

        btn_logOut.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent( getActivity().getApplication(), LoginActivity.class );
                startActivity( intent );
            }
        } );
        return v;
    }

    public void pickImage() {
        Intent intent = new Intent( Intent.ACTION_GET_CONTENT );
        intent.setType( "image/*" );
        startActivityForResult( intent, PICK_PHOTO_FOR_AVATAR );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }


            try {

                StorageReference storageRef = FirebaseStorage.getInstance( "gs://dallamada.appspot.com/" ).getReference();
                StorageReference ref = storageRef.child( "users/" + user.getEmail() + ".jpg" );
                InputStream inputStream = getContext().getContentResolver().openInputStream( data.getData() );
                UploadTask uploadTask = ref.putStream( inputStream );
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child("users/" + user.getEmail() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mFirestore.collection("users").document( "test@gmail.com").update( "imgProfile",String.valueOf( uri ) );

                                // Got the download URL for 'users/me/profile.png'
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public void checkingDarkMode() {

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                switchBtn.setChecked( false );
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                switchBtn.setChecked( true );
                break;
        }
    }
}