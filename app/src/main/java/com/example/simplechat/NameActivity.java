package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NameActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextInputLayout mName;
    private Button mSavename;

    private DatabaseReference mDatabase;
    private FirebaseUser mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        mToolbar = findViewById(R.id.name_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("User name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = findViewById(R.id.name_input);
        mSavename = findViewById(R.id.btnSavechange);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(uid);

        mSavename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mName.getEditText().getText().toString();
                mDatabase.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(NameActivity.this, "Success!", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(NameActivity.this, "You got some error.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
