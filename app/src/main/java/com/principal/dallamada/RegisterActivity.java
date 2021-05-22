package com.principal.dallamada;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private EditText editTextUsername, editTextEmail, editTextPassword, editTextPasswordConfirm;
    private TextView textViewSignIn;
    private Button btn_register;

    private ProgressBar progressBarRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Inicialización de Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        editTextPasswordConfirm = findViewById(R.id.editTextConfirmPasswordRegister);
        textViewSignIn = findViewById(R.id.textViewSignIn);
        btn_register = findViewById(R.id.btn_register);
        progressBarRegister = findViewById(R.id.progressBarRegister);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();

                progressBarRegister.setVisibility(View.VISIBLE);
                createAccount(email, username, password);
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //Método para añadir un usuario a la base de datos de Firebase Firestore
    private void addUserDatabase(String email, String username, String password) {
        if (validateValues()) {
            mFirestore = FirebaseFirestore.getInstance();

            mFirestore.collection("users").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "savedUserDataInDatabase:success");

                                //Genera numero aleatorio entre 0 y 9999, con 4 dígitos
                                String randomNumber = String.format("%04d", new Random().nextInt(9999));

                                Map<String, Object> user = new HashMap<>();
                                user.put("username", username + "#" + randomNumber);
                                user.put("email" , email);
                                user.put("imgProfile", "https://firebasestorage.googleapis.com/v0/b/dallamada.appspot.com/o/din.png?alt=media&token=8b57121c-ce71-4554-b1d7-8383559ea7ed");
                                mFirestore.collection("users").document(email).set(user);
                                Log.d(TAG, "savedUserDataInDatabase:success");

                                progressBarRegister.setVisibility(View.GONE);
                            }

                        }
                    });
        }
    }

    //Método para la creación de usuario
    private void createAccount(String email, String username, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            addUserDatabase(email, username, password);
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(RegisterActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                editTextEmail.setError(getString(R.string.error_user_used));
                                editTextEmail.requestFocus();
                                Toast.makeText(RegisterActivity.this, "Email existente", Toast.LENGTH_SHORT).show();
                                progressBarRegister.setVisibility(View.GONE);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Error de registro", Toast.LENGTH_SHORT).show();
                                progressBarRegister.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }


    //Metodo de comprobación de valores de los campos
    private boolean validateValues() {
        boolean validate = true;

        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordConfirm = editTextPasswordConfirm.getText().toString().trim();

        //Comprobación del nombre de usuario
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError(getString(R.string.error_empty_field));
            editTextUsername.requestFocus();
            validate = false;
        } else {
            editTextUsername.setError(null);
        }

        //Comprobación del email
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.error_empty_field));
            editTextEmail.requestFocus();
            validate = false;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError(getString(R.string.error_email_not_valid));
                editTextEmail.requestFocus();
                validate = false;
            } else {
                editTextEmail.setError(null);
            }
        }

        //Comprobación de la contraseña
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(getString(R.string.error_empty_field));
            editTextPassword.requestFocus();
            validate = false;
        } else {
            if (password.length() < 6) {
                editTextPassword.setError(getString(R.string.error_password_short));
                editTextPassword.requestFocus();
                validate = false;
            } else {
                editTextPassword.setError(null);
            }
        }

        //Comprobación de la confirmación de contraseña
        if (TextUtils.isEmpty(passwordConfirm)) {
            editTextPasswordConfirm.setError(getString(R.string.error_empty_field));
            editTextPasswordConfirm.requestFocus();
            validate = false;
        } else {
            if (!password.equals(passwordConfirm)) {
                editTextPasswordConfirm.setError(getString(R.string.error_password_not_match));
                editTextPasswordConfirm.requestFocus();
                validate = false;
            } else {
                editTextPasswordConfirm.setError(null);
            }
        }
        return validate;

    }

}