package com.example.geekymad.blog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SigninActivity";
    ProgressBar progressBar;
    EditText emailSignin, passwordSignin;
    Button signinButton;

    /*Firebase declarations*/
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();

        emailSignin = (EditText) findViewById(R.id.email_signin);
        passwordSignin = (EditText) findViewById(R.id.password_signin);
        signinButton = (Button) findViewById(R.id.signin_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_signin);


        // [START auth_state_listener]
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
        // [END auth_state_listener]

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(emailSignin.getText().toString().trim(), passwordSignin.getText().toString().trim());
            }
        });
        checkUser();
    }


    private void checkUser() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
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
    private void signIn(String email, String password) {

        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            Toast.makeText(SigninActivity.this, "Signed In Successfully",
                                    Toast.LENGTH_SHORT).show();
                            Intent chatIntent = new Intent(SigninActivity.this, ChatActivity.class);
                            startActivity(chatIntent);
                            finish();
                        }
                        else {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }// [END sign_in_with_email]

    private boolean validateForm() {
        boolean valid = true;

        String mEmail = emailSignin.getText().toString();
        if (TextUtils.isEmpty(mEmail)) {
            emailSignin.setError("Required.");
            valid = false;
        } else {
            emailSignin.setError(null);
        }

        String mPassword = passwordSignin.getText().toString();
        if (TextUtils.isEmpty(mPassword)) {
            passwordSignin.setError("Required.");
            valid = false;
        } else {
            passwordSignin.setError(null);
        }

        return valid;
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]


    private void hideProgressDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }
}
