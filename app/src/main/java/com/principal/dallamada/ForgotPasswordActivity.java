package com.principal.dallamada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPassword";

    private FirebaseAuth mAuth;

    private Button btn_resetPassword;
    private EditText editTextEmail;
    private TextView textViewBackLogin;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        textViewBackLogin = findViewById(R.id.textViewBackLogin);
        editTextEmail = findViewById(R.id.editTextEmailForgotPass);
        btn_resetPassword = findViewById(R.id.btn_resetPassword);
        progressBar = findViewById(R.id.progressBarForgotPass);

        textViewBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateValues()) {
                    String email = editTextEmail.getText().toString().trim();
                    progressBar.setVisibility(View.VISIBLE);
                    sendEmail(email);
                }
            }
        });
    }

    //Método que enviará un email para restablecer la contraseña
    private void sendEmail(String email) {
        mAuth = FirebaseAuth.getInstance();

        //Método para que Firebase envíe el correo en el idioma del dispositivo
        mAuth.useAppLanguage();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {
                                                   Log.d(TAG, "ForgotPasswordEmail:success");
                                                   Toast.makeText(ForgotPasswordActivity.this, getText(R.string.forgotPass_send_email), Toast.LENGTH_SHORT).show();
                                                   progressBar.setVisibility(View.GONE);
                                                   Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                                   startActivity(intent);
                                               } else {
                                                   Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                   Toast.makeText(ForgotPasswordActivity.this, getText(R.string.error_send_recover_pass_email), Toast.LENGTH_SHORT).show();
                                                   progressBar.setVisibility(View.GONE);
                                               }
                                           }
                                       }


                );


    }

    //Método de comprobación de valores de los campos
    private boolean validateValues() {
        boolean validate = true;

        String email = editTextEmail.getText().toString().trim();

        //Checking username input
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

        return validate;
    }
}