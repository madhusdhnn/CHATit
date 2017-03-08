package com.example.geekymad.blog;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {

    private static final String ANONYMOUS = "anonymous";
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    LinearLayoutManager mManager;
    FirebaseRecyclerAdapter<ChatMessages, ChatHolder> mAdapter;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mChatRef;
    private String mEmail;
    private Button mSendButton;
    private EditText mMessageEditText;
    private RecyclerView mMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mEmail = ANONYMOUS;

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    onSignedInInitialize(user.getEmail());
                    attachRecyclerViewAdapter();
                } else {
                    //signed out
                    onSignedOutCleanup();
                }
            }
        };
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mChatRef = mDatabaseReference.child("message");

        mMessageEditText = (EditText) findViewById(R.id.message);
        mSendButton = (Button) findViewById(R.id.send);

        // Enable Send button only when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                    mSendButton.setBackground(getDrawable(R.drawable.send_button));
                } else {
                    mSendButton.setEnabled(false);
                    mSendButton.setBackground(getDrawable(R.drawable.send_button_disabled));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send messages when clicked
                ChatMessages messages = new ChatMessages(mMessageEditText.getText().toString().trim(), mEmail);
                mChatRef.push().setValue(messages);
                mMessageEditText.setText("");
            }
        });

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);
        mMessages = (RecyclerView) findViewById(R.id.messagesList);
        mMessages.setHasFixedSize(false);
        mMessages.setLayoutManager(mManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    private void attachRecyclerViewAdapter() {
        Query lastFifty = mChatRef.limitToLast(50);
        mAdapter = new FirebaseRecyclerAdapter<ChatMessages, ChatHolder>(ChatMessages.class, R.layout.item_message, ChatHolder.class, lastFifty) {
            @Override
            protected void populateViewHolder(ChatHolder chatHolder, ChatMessages chatMessages, int i) {

                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && chatMessages.getEmail().equals(user.getEmail())) {
                    chatHolder.isSender(true);
                } else {
                    chatHolder.isSender(false);
                }

                chatHolder.setEmail(chatMessages.getEmail());
                chatHolder.setText(chatMessages.getText());
            }
        };
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mMessages, null, mAdapter.getItemCount());
            }
        });
        mMessages.setAdapter(mAdapter);
    }

    private void onSignedOutCleanup() {
        mEmail = ANONYMOUS;
    }

    private void onSignedInInitialize(String email) {
        mEmail = email;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.account_menu:
                signOut();
                Intent homeIntent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                return true;
            case R.id.account_profile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onBackPressed() {
        //empty..nothing to do.
}
