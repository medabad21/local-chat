package com.example.localchat;

public class User {
    private String userId;
    private String username;
    private String email;
    private String profileImageUrl;
    private String status;
    private long lastSeen;
    private boolean online;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String userId, String username, String email, String profileImageUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.status = "Hey there! I'm using Local Chat";
        this.online = false;
        this.lastSeen = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getLastSeen() { return lastSeen; }
    public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }

    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }
}
