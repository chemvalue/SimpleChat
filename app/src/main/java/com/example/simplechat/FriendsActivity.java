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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mFriendsList;
    private DatabaseReference mUsersDatabase, mFriendsDatabase;
    private FirebaseUser mAuth;
    private String current_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mAuth.getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_uid);

        mToolbar = findViewById(R.id.friends_appbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFriendsList = findViewById(R.id.friends_list);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(mUsersDatabase, Users.class).build();

        FirebaseRecyclerAdapter<Users, FriendsActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, FriendsActivity.UsersViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsActivity.UsersViewHolder holder, int position, @NonNull final Users model) {
                holder.setName(model.getName());

                final String uid = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(FriendsActivity.this, ChatsActivity.class);
                        profileIntent.putExtra("User_id", uid);
                        profileIntent.putExtra("user_name", model.getName());
                        startActivity(profileIntent);

                    }
                });
            }

            @NonNull
            @Override
            public FriendsActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);
                return new UsersViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        mFriendsList.setAdapter(firebaseRecyclerAdapter);

    }

    private static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;

        UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
    }
}
