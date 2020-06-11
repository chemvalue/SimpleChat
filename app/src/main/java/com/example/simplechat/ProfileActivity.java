package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView mDisplayName;
    private CircleImageView mProfileImage;
    private Button btnSendRequest;
    private String current_state;

    private DatabaseReference mDatabase, mFriendReqDb, mFriendDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String uid = getIntent().getStringExtra("User_id");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
        mFriendReqDb = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = findViewById(R.id.user_profile_image);
        mDisplayName = findViewById(R.id.user_display_name);
        btnSendRequest = findViewById(R.id.btnFriendRequest);

        current_state = "not friend";

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String displayname = dataSnapshot.child("name").getValue().toString();
                mDisplayName.setText(displayname);

                mFriendReqDb.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(uid)){
                            String request_type = dataSnapshot.child(uid).child("request_type").getValue().toString();
                            if (request_type.equals("received")){
                                current_state = "Request received";
                                btnSendRequest.setText("Accept request");
                            }else if (request_type.equals("sent")){
                                current_state = "sent request";
                                btnSendRequest.setText("Cancel request");
                            }
                        } else {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(uid)){
                                        current_state = "friends";
                                        btnSendRequest.setText("UnFriend");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnSendRequest.setEnabled(false);

                if (current_state.equals("not friend")){
                    mFriendReqDb.child(mCurrentUser.getUid()).child(uid).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mFriendReqDb.child(uid).child(mCurrentUser.getUid()).child("request_type").setValue("received")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        current_state = "sent request";
                                        btnSendRequest.setText("Cancel request");
                                        Toast.makeText(ProfileActivity.this,"Request send successfully!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this,"Failed sending request!",Toast.LENGTH_SHORT).show();
                            }
                            btnSendRequest.setEnabled(true);
                        }
                    });
                }

                if (current_state.equals("sent request")){
                    mFriendReqDb.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDb.child(uid).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btnSendRequest.setEnabled(true);
                                    current_state = "not friend";
                                    btnSendRequest.setText("Send request");
                                    Toast.makeText(ProfileActivity.this,"Request canceled!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

                if (current_state.equals("Request received")){
                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(uid).setValue(current_date)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(uid).child(mCurrentUser.getUid()).setValue(current_date)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendReqDb.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDb.child(uid).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    btnSendRequest.setEnabled(true);
                                                    current_state = "friends";
                                                    btnSendRequest.setText("UnFriend");
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

    }
}
