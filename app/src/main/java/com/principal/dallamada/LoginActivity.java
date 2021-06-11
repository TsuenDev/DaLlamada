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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";

    private FirebaseAuth mAuth;

    private EditText editTextEmail, editTextPassword;
    private TextView textViewForgotPass, textViewRegister;
    private Button btn_login;

    private ProgressBar progressBarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmailLogin);
        editTextPassword = findViewById(R.id.editTextPasswordLogin);
        textViewForgotPass = findViewById(R.id.textViewForgotPass);
        textViewRegister = findViewById(R.id.textViewSignIn);
        btn_login = findViewById(R.id.btn_login);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        textViewForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateValues()) {
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();
                    progressBarLogin.setVisibility(View.VISIBLE);
                    logIn(email, password);
                }
            }
        });

    }


    //Método de comprobación el estado de un usuario (si se ha iniciado sesión previamente o no)
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    //Método de inicio de sesión
    private void logIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show();
                            progressBarLogin.setVisibility(View.GONE);
                        }
                    }
                });
    }

    //Método de comprobación de valores de los campos
    private boolean validateValues() {
        boolean validate = true;

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //Comprobación de email
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

        //Comprobación de contraseña
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(getString(R.string.error_empty_field));
            editTextPassword.requestFocus();
            validate = false;
        } else {
            editTextPassword.setError(null);
        }

        return validate;
    }

}