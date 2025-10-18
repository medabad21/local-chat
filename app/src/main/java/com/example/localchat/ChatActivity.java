package com.example.localchat;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localchat.R;
import com.example.localchat.MessagesAdapter;
import com.example.localchat.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageView backButton, attachImageButton, removeImageButton, selectedImageView;
    private CircleImageView userImageView;
    private TextView userNameTextView, userStatusTextView;
    private RecyclerView messagesRecyclerView;
    private EditText messageEditText;
    private FloatingActionButton sendButton;
    private LinearLayout imagePreviewLayout;     

    private MessagesAdapter messagesAdapter;
    private List<Message> messagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference messagesReference, usersReference;
    private StorageReference storageReference;

    private String currentUserId, receiverUserId, receiverName, receiverImage;
    private String chatRoomId;
    private Uri selectedImageUri;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get receiver data from intent
        receiverUserId = getIntent().getStringExtra("userId");
        receiverName = getIntent().getStringExtra("username");
        receiverImage = getIntent().getStringExtra("profileImage");

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        // Create chat room ID (consistent for both users)
        chatRoomId = createChatRoomId(currentUserId, receiverUserId);

        messagesReference = FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(chatRoomId);
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        initializeViews();
        setupRecyclerView();
        setupImagePicker();
        setupClickListeners();
        loadMessages();
        checkUserStatus();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        userImageView = findViewById(R.id.userImageView);
        userNameTextView = findViewById(R.id.userNameTextView);
        userStatusTextView = findViewById(R.id.userStatusTextView);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        attachImageButton = findViewById(R.id.attachImageButton);
        imagePreviewLayout = findViewById(R.id.imagePreviewLayout);
        selectedImageView = findViewById(R.id.selectedImageView);
        removeImageButton = findViewById(R.id.removeImageButton);

        // Set receiver info
        userNameTextView.setText(receiverName);
        if (receiverImage != null && !receiverImage.isEmpty()) {
            Glide.with(this).load(receiverImage).into(userImageView);
        }
    }

    private void setupRecyclerView() {
        messagesList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messagesList, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(messagesAdapter);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        selectedImageView.setImageURI(uri);
                        imagePreviewLayout.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        sendButton.setOnClickListener(v -> sendMessage());

        attachImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        removeImageButton.setOnClickListener(v -> {
            selectedImageUri = null;
            imagePreviewLayout.setVisibility(View.GONE);
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();

        if (TextUtils.isEmpty(messageText) && selectedImageUri == null) {
            return;
        }

        sendButton.setEnabled(false);

        if (selectedImageUri != null) {
            uploadImageAndSendMessage(messageText);
        } else {
            sendTextMessage(messageText, "");
        }
    }

    private void uploadImageAndSendMessage(String messageText) {
        String fileName = "chat_images/" + System.currentTimeMillis() + ".jpg";
        StorageReference fileReference = storageReference.child(fileName);

        fileReference.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            sendTextMessage(messageText, imageUrl);
                            selectedImageUri = null;
                            imagePreviewLayout.setVisibility(View.GONE);
                        }))
                .addOnFailureListener(e -> {
                    sendButton.setEnabled(true);
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendTextMessage(String messageText, String imageUrl) {
        String messageType = imageUrl.isEmpty() ? "text" : "image";
        Message message = new Message(currentUserId, receiverUserId, messageText, imageUrl, messageType);

        messagesReference.push().setValue(message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageEditText.setText("");
                        messagesRecyclerView.scrollToPosition(messagesList.size() - 1);
                    } else {
                        Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                    sendButton.setEnabled(true);
                });
    }

    private void loadMessages() {
        messagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message != null) {
                        messagesList.add(message);
                    }
                }

                messagesAdapter.notifyDataSetChanged();
                if (!messagesList.isEmpty()) {
                    messagesRecyclerView.scrollToPosition(messagesList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this,
                        "Failed to load messages",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        usersReference.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean online = snapshot.child("online").getValue(Boolean.class);
                    if (online != null && online) {
                        userStatusTextView.setText("Online");
                    } else {
                        Long lastSeen = snapshot.child("lastSeen").getValue(Long.class);
                        if (lastSeen != null) {
                            userStatusTextView.setText("Last seen: " + getTimeAgo(lastSeen));
                        } else {
                            userStatusTextView.setText("Offline");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private String createChatRoomId(String userId1, String userId2) {
        // Create consistent chat room ID for both users
        List<String> userIds = Arrays.asList(userId1, userId2);
        userIds.sort(String::compareTo);
        return userIds.get(0) + "_" + userIds.get(1);
    }

    private String getTimeAgo(long time) {
        long diff = System.currentTimeMillis() - time;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d ago";
        if (hours > 0) return hours + "h ago";
        if (minutes > 0) return minutes + "m ago";
        return "just now";
    }
}
