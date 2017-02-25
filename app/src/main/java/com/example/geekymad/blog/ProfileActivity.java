package com.example.geekymad.blog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        textView = (TextView) findViewById(R.id.email_Text_view);
        showUser();
    }

    private void showUser() {
         user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            String email = user.getEmail();
            textView.setText(email);
        }
    }
}
