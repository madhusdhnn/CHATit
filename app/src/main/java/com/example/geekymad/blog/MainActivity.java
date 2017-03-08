package com.example.geekymad.blog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

public class MainActivity extends AppCompatActivity {

    Button signInButton, getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
        
         if (!isNetworkAvailable(getApplicationContext())){
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

        signInButton = (Button) findViewById(R.id.sign_in_button);
        getStartedButton = (Button) findViewById(R.id.get_started_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(signInIntent);
            }
        });

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getStartedIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(getStartedIntent);
            }
        });
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null){
                return networkInfo.isConnected();
            }
        }
        return false;
    }
}
