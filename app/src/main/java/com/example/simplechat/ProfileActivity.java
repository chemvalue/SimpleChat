package com.example.simplechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView mDisplayID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String uid = getIntent().getStringExtra("User_id");
        mDisplayID = findViewById(R.id.profile_displayName);
        mDisplayID.setText(uid);

    }
}
