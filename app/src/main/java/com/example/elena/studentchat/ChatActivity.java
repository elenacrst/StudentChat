package com.example.elena.studentchat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.elena.studentchat.adapters.MessagesAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    EditText mMessageEditText;
    ImageButton mSendButton;
    RecyclerView mRecyclerView;
    MessagesAdapter mChatAdapter;
    List<ChatMessage> mData = new ArrayList<>();
    ProgressBar mLoadingIndicator;

    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mMessagesRef;
    private ChildEventListener mChildListener;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN= 1;
    private static final int RC_PHOTO_PICKER= 2;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;
    String mUsername;
    private static final String UNKNOWN_USER = "unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mRecyclerView = (RecyclerView)findViewById(R.id.messageRecyclerView);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.progressBar);

        FirebaseMessaging.getInstance().subscribeToTopic("notified");
        mUsername = UNKNOWN_USER;

       /* mMessageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    mRecyclerView.smoothScrollToPosition(mData.size()-1);
                }
            }
        });*/

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        setupRecyclerView();
        if (mData!=null && mData.size()>0)
            mRecyclerView.smoothScrollToPosition(mData.size()-1);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    signInInitialize(user.getDisplayName());
                }else{
                    signOutCleanup();

                    List<AuthUI.IdpConfig> authList = new ArrayList<>();
                    authList.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
                    authList.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(authList)
                                    .build(),
                            RC_SIGN_IN
                    );
                }
            }
        };
        mLoadingIndicator.setVisibility(ProgressBar.INVISIBLE);

        mFirebaseDb = FirebaseDatabase.getInstance();
        mMessagesRef = mFirebaseDb.getReference().child("messages");

    }

    private void setupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mChatAdapter= new MessagesAdapter();
        mRecyclerView.setAdapter(mChatAdapter);
    }

    public void sendMessage(View view) {

        String text = mMessageEditText.getText().toString();
        ChatMessage message = new ChatMessage(text, mUsername, null);
        mMessagesRef.push().setValue(message);
        mMessageEditText.setText("");

    }

    public void sendImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                RC_PHOTO_PICKER);
    }

    private void attachReadListener(){
        if (mChildListener == null){
            mChildListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    mData.add(message);
                    mChatAdapter.setData(mData);
                    mRecyclerView.smoothScrollToPosition(mData.size()-1);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesRef.addChildEventListener(mChildListener);
        }

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override

            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {

                if (mData!=null && mData.size()>0)
                mRecyclerView.scrollToPosition(mData.size()-1);

            }
        });

    }

    private void signInInitialize(String username){
        mUsername = username;
        attachReadListener();
        if (mData!=null && mData.size()>0)
            mRecyclerView.smoothScrollToPosition(mData.size()-1);
    }

    private void signOutCleanup(){
        mUsername = UNKNOWN_USER;
        mChatAdapter.setData(null);
        detachReadListener();
    }

    private void detachReadListener(){
        if (mChildListener != null){
            mMessagesRef.removeEventListener(mChildListener);
            mChildListener = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if (resultCode== RESULT_OK){
                Toast.makeText(this, "Signed in",Toast.LENGTH_SHORT).show();
                attachReadListener();
            }else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }else if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            StorageReference photoRef= mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    if (downloadUri!=null){
                        ChatMessage message = new ChatMessage(null, mUsername, downloadUri.toString());
                        mMessagesRef.push().setValue(message);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth.addAuthStateListener(mAuthStateListener);
        attachReadListener();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);

        detachReadListener();
        mChatAdapter.setData(null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
