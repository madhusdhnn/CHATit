package com.example.geekymad.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    EditText email,password;
    Button registerButton;

    private FirebaseAuth.AuthStateListener mAuthListener;
    ProgressBar progressBar;
    private static final String TAG = "GetStarted";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.register_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(email.getText().toString().trim(), password.getText().toString().trim());
            }
        });

        checkUser();
    }

    private void checkUser() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private boolean validateForm() {
        boolean valid = true;

        String mEmail = email.getText().toString();
        String mPassword = password.getText().toString();
        /*if(TextUtils.isEmpty(mUsername)){
            username.setError("Required.");
            valid = false;
        }else{
            username.setError(null);
        }

        if (TextUtils.isEmpty(mEmail)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }


        if (TextUtils.isEmpty(mPassword)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }*/

        if(TextUtils.isEmpty(mEmail) && TextUtils.isEmpty(mPassword)){
            email.setError("Required.");
            password.setError("Required.");
            valid = false;
        }else {
            email.setError(null);
            password.setError(null);
        }



        return valid;
    }

    private void createAccount(String email, String password) {


        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                            Intent chatIntent = new Intent(SignupActivity.this, ChatActivity.class);
                            startActivity(chatIntent);
                            finish();
                        }else{
                            Toast.makeText(SignupActivity.this, "Try Again!", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }

                });

    }

    private void hideProgressDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
