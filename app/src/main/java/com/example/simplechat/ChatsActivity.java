package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatsActivity extends AppCompatActivity {

    private String mChatUser;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private String currentUid;

    private ImageButton btnChatSend;
    private EditText mChatMessage;
    private RecyclerView mMessagesList;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        mToolbar = findViewById(R.id.chat_app_bar);
        String mUserName = getIntent().getStringExtra("user_name");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mUserName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();

        btnChatSend = findViewById(R.id.btn_send_message);
        mChatMessage = findViewById(R.id.chat_message);
        mMessagesList = findViewById(R.id.message_list);
        mLinearLayout = new LinearLayoutManager(this);

        mAdapter = new MessageAdapter(messagesList);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mMessagesList.setAdapter(mAdapter);

        mChatUser = getIntent().getStringExtra("User_id");

        loadMessage();

        btnChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void loadMessage() {
        mRootRef.child("message").child(currentUid).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {

        String message = mChatMessage.getText().toString();
        if (!TextUtils.isEmpty(message)){
            String current_user_ref = "message/" + currentUid + "/" + mChatUser;
            String chat_user_ref = "message/" + mChatUser + "/" + currentUid;

            DatabaseReference user_message_push = mRootRef.child("message").child(currentUid).child(mChatUser).push();
            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", currentUid);

            mChatMessage.setText("");

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }

    }
}
