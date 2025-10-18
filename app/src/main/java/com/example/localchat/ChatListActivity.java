package com.example.localchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localchat.R;
import com.example.localchat.UsersAdapter;
import com.example.localchat.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private TextView emptyTextView;
    private ProgressBar progressBar;
    private FloatingActionButton newChatFab;
    private Toolbar toolbar;

    private UsersAdapter usersAdapter;
    private List<User> usersList;

    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersReference = FirebaseDatabase.getInstance().getReference("Users");

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadUsers();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        progressBar = findViewById(R.id.progressBar);
        newChatFab = findViewById(R.id.newChatFab);

        newChatFab.setOnClickListener(v -> {
            // Implement new chat functionality
            Toast.makeText(this, "Select a user to start chatting", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        usersList = new ArrayList<>();
        usersAdapter = new UsersAdapter(this, usersList, user -> {
            // Navigate to chat activity
            Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
            intent.putExtra("userId", user.getUserId());
            intent.putExtra("username", user.getUsername());
            intent.putExtra("profileImage", user.getProfileImageUrl());
            startActivity(intent);
        });

        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(usersAdapter);
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    // Don't add current user to the list
                    if (user != null && !user.getUserId().equals(currentUserId)) {
                        usersList.add(user);
                    }
                }

                usersAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (usersList.isEmpty()) {
                    emptyTextView.setVisibility(View.VISIBLE);
                    usersRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                    usersRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChatListActivity.this,
                        "Failed to load users: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // Navigate to profile activity
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Update online status
        usersReference.child(currentUserId).child("online").setValue(false);
        usersReference.child(currentUserId).child("lastSeen").setValue(System.currentTimeMillis());

        mAuth.signOut();

        Intent intent = new Intent(ChatListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set user online
        usersReference.child(currentUserId).child("online").setValue(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Set user offline
        usersReference.child(currentUserId).child("online").setValue(false);
        usersReference.child(currentUserId).child("lastSeen").setValue(System.currentTimeMillis());
    }
}
