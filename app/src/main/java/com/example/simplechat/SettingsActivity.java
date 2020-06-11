package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView mImage;
    private TextView mDisplayname;
    private Button btnChangeDisplayname;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mImage = findViewById(R.id.profile_image);
        mDisplayname = findViewById(R.id.display_name);
        btnChangeDisplayname = findViewById(R.id.btnChangeName);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(current_uid);
        mDatabase.keepSynced(true);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb = dataSnapshot.child("thumb_image").getValue().toString();

                mDisplayname.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnChangeDisplayname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent name_intent = new Intent(SettingsActivity.this,NameActivity.class);
                startActivity(name_intent);
            }
        });

    }
}
