package com.principal.dallamada.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.principal.dallamada.LoginActivity;
import com.principal.dallamada.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int PICK_PHOTO_FOR_AVATAR = 0;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ImageView imgProfile;
    private TextView textViewUsername, textViewEmail, textViewChangeImage;
    private Switch switchBtn;
    private Button btn_logOut;

    private ArrayAdapter<String> arrayAdapterGroups;
    private List<String> arrayListGroups = new ArrayList<String>();

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = v.findViewById(R.id.imageProfile);
        textViewUsername = v.findViewById(R.id.textViewUsernameProfile);
        textViewEmail = v.findViewById(R.id.textViewEmailProfile);
        switchBtn = v.findViewById(R.id.switchDarkMode);
        btn_logOut = v.findViewById(R.id.btn_log_out);
        textViewChangeImage = v.findViewById(R.id.changeImageProfile);
        TextView selectecGroup = v.findViewById(R.id.tv_selectecGroup);
        EditText findGroup = v.findViewById(R.id.et_find_group);
        Button buttonFindGroup = v.findViewById(R.id.btn_find_group);

        Spinner spinnerGroups = v.findViewById(R.id.sp_groups);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        //Boton para cambiar la imagen de perfil
        textViewChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

/*        buttonFindGroup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupToSearch = findGroup.getText().toString();
                mFirestore.collection("groups")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                boolean isFinded = false;
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getId().equals(  groupToSearch)){
                                            isFinded = true;
                                        }
                                    }
                                    if (!isFinded){
                                        findGroup.getText().clear();
                                    }
                                    //TRABAJANDO AQUIIIIIIIIIIIIIIIIIIIIIIII
                                    Map<String, Object> e = new HashMap<>();
                                    mFirestore.collection("users").document(user.getEmail()).collection( "groups" ).document(groupToSearch).set(e);
                                    mFirestore.collection( "users" ).document( user.getEmail() ).update( "selectedGroup", groupToSearch );
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        } );*/

        mFirestore.collection("users")
                .document(user.getEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                textViewUsername.setText("Nombre de usuario " + snapshot.getString("username"));
                textViewEmail.setText("Email " + snapshot.getString("email"));
                Glide.with(ProfileFragment.this).load(snapshot.getString("imgProfile")).into(imgProfile);
                selectecGroup.setText(snapshot.getString("selectedGroup"));

                mFirestore.collection("users").document(user.getEmail()).collection("groups").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                //Limpiamos el spinner para que no se duplique los grupos
                                arrayAdapterGroups.clear();
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        //Recogemos todos los grupos
                                        arrayAdapterGroups.add(document.getId());
                                    }
                                }
                            }
                        });

                arrayAdapterGroups = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, arrayListGroups);

                // El spinner muestra todos los grupos anteriormente recogidos
                spinnerGroups.setAdapter(arrayAdapterGroups);

                // El spinner se queda en el grupo que esta seleccionado
                spinnerGroups.setSelection(arrayListGroups.indexOf(snapshot.getString("selectedGroup")));

                spinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        //Se cambia el grupo seleccionado en el front
                        selectecGroup.setText(parentView.getItemAtPosition(position).toString());

                        //Se cambia el grupo seleccionado en el Back
                        mFirestore.collection("users").document(user.getEmail()).update("selectedGroup", parentView.getItemAtPosition(position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }
                });
            }

        });


        checkingDarkMode();
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        btn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }


            try {

                StorageReference storageRef = FirebaseStorage.getInstance("gs://dallamada.appspot.com/").getReference();

                StorageReference ref = storageRef.child("users/" + user.getEmail() + ".jpg");

                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());

                UploadTask uploadTask = ref.putStream(inputStream);

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
                                mFirestore.collection("users").document(user.getEmail()).update("imgProfile", String.valueOf(uri));
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
                switchBtn.setChecked(false);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                switchBtn.setChecked(true);
                break;
        }
    }
}