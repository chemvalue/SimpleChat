package com.example.simplechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewpage;
    private SectionsPageAdapter mSectionsPageAdapter;
    private TabLayout mTablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Simple Chat");

        mViewpage = (ViewPager) findViewById(R.id.main_tab_page);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewpage.setAdapter(mSectionsPageAdapter);
        mTablayout = findViewById(R.id.main_tabs);
        mTablayout.setupWithViewPager(mViewpage);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            sendToStart();
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_btn){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if (item.getItemId() == R.id.main_setting_btn){
            Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }
        if (item.getItemId() == R.id.main_all_btn){
            Intent usersIntent = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(usersIntent);
        }
        if (item.getItemId() == R.id.main_friend_btn){
            Intent friendIntent = new Intent(MainActivity.this,FriendsActivity.class);
            startActivity(friendIntent);
        }
        return true;
    }
}
