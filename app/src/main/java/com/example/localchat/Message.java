package com.example.localchat;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String messageText;
    private String imageUrl;
    private long timestamp;
    private boolean seen;
    private String messageType; // "text" or "image"

    public Message() {
        // Required empty constructor for Firebase
    }

    public Message(String senderId, String receiverId, String messageText, String imageUrl, String messageType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.imageUrl = imageUrl;
        this.messageType = messageType;
        this.timestamp = System.currentTimeMillis();
        this.seen = false;
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
}
