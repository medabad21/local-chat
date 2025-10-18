package com.example.localchat;

import java.util.List;

public class ChatRoom {
    private String chatRoomId;
    private List<String> userIds;
    private String lastMessage;
    private long lastMessageTimestamp;
    private int unreadCount;

    public ChatRoom() {
        // Required empty constructor for Firebase
    }

    public ChatRoom(String chatRoomId, List<String> userIds) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMessage = "";
        this.lastMessageTimestamp = System.currentTimeMillis();
        this.unreadCount = 0;
    }

    // Getters and Setters
    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }

    public List<String> getUserIds() { return userIds; }
    public void setUserIds(List<String> userIds) { this.userIds = userIds; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public long getLastMessageTimestamp() { return lastMessageTimestamp; }
    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
