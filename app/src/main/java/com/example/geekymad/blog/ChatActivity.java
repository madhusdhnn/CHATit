package com.example.geekymad.blog;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.R.attr.type;

public class ChatActivity extends AppCompatActivity {

    //constants
    private static final String ANONYMOUS = "anonymous";
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER = 2;
    private static final int RC_TEXT_MESSAGE = 1;
    //firebase declarations
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<ChatMessages, ChatHolder> mAdapter;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private LinearLayoutManager mManager;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mChatRef;
    private String mEmail;
    private Button mSendButton;
    private EditText mMessageEditText;
    private RecyclerView mMessages;
    private ImageButton mPhotoPickerButton;

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
        mStorage = FirebaseStorage.getInstance();

        mDatabaseReference = mFirebaseDatabase.getReference();
        mChatRef = mDatabaseReference.child("message");
        mStorageReference = mStorage.getReference().child("chat_photos");


       mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.message);
        mSendButton = (Button) findViewById(R.id.send);

        progressBar = (ProgressBar) findViewById(R.id.progressBarChat);

        showProgressBar();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressBar();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //empty
            }
        });

       mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
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
                ChatMessages messages = new ChatMessages(mMessageEditText.getText().toString().trim(), mEmail, null);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            // when an image is selected, it comes as a uri data..so create an uri object
            Uri selectedImageUri = data.getData();
            // reference to the specific photo
            // getting the last path segment
            //means, suppose in the uri : //content://local/foo/4, 4 would be the image file name which we wanted here.
            final StorageReference photoRef = mStorageReference.child(selectedImageUri.getLastPathSegment());
            //upload to firebase storage
            photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl != null){
                        ChatMessages messages = new ChatMessages(null, mEmail, downloadUrl.toString());
                        mDatabaseReference.push().setValue(messages);
                    }
                }
            });

        }
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

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void attachRecyclerViewAdapter() {
        Query lastTwoHundred = mChatRef.limitToLast(200);
        /* FirebaseRecyclerAdapter<>(ArrayList<> keys, Layout_item, ViewHolder, Query)*/
        mAdapter = new FirebaseRecyclerAdapter<ChatMessages, ChatHolder>(ChatMessages.class, R.layout.item_message, ChatHolder.class, lastTwoHundred) {

            @Override
            protected void populateViewHolder(ChatHolder chatHolder, ChatMessages chatMessages, int position) {

                int viewType = chatHolder.getItemViewType();
                if(viewType == RC_PHOTO_PICKER){
                    chatHolder.isPhoto(true);
                }
                else if(viewType == RC_TEXT_MESSAGE){
                    chatHolder.isPhoto(false);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && chatMessages.getEmail().equals(user.getEmail()))
                    {
                        chatHolder.isSender(true);
                    } else {
                        chatHolder.isSender(false);
                    }
                }

                chatHolder.setEmail(chatMessages.getEmail());
                chatHolder.setText(chatMessages.getText());
                chatHolder.setPhotoUrl(Uri.parse(chatMessages.getPhotoUrl()));
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
}
