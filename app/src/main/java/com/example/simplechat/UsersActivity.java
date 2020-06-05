package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        mToolbar = findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersList = findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(mDatabase, Users.class).build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                options
                ) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                holder.setName(model.getName());

                final String uid = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("User_id", uid);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);
                return new UsersViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    private class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
    }
}
