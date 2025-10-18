package com.example.localchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localchat.R;
import com.example.localchat.Message;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<Message> messagesList;
    private String currentUserId;

    public MessagesAdapter(Context context, List<Message> messagesList, String currentUserId) {
        this.context = context;
        this.messagesList = messagesList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messagesList.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_message, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_messages_adapter, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messagesList.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    // Sent Message ViewHolder
    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timeTextView;
        ImageView messageImageView, messageStatusIcon;
        MaterialCardView imageCard, messageCard;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            messageStatusIcon = itemView.findViewById(R.id.messageStatusIcon);
            imageCard = itemView.findViewById(R.id.imageCard);
            messageCard = itemView.findViewById(R.id.messageCard);
        }

        public void bind(Message message) {
            if (message.getMessageType().equals("image") && message.getImageUrl() != null) {
                imageCard.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUrl()).into(messageImageView);
            } else {
                imageCard.setVisibility(View.GONE);
            }

            if (message.getMessageText() != null && !message.getMessageText().isEmpty()) {
                messageCard.setVisibility(View.VISIBLE);
                messageTextView.setText(message.getMessageText());
            } else {
                messageCard.setVisibility(View.GONE);
            }

            timeTextView.setText(formatTime(message.getTimestamp()));

            if (message.isSeen()) {
                messageStatusIcon.setImageResource(R.drawable.ic_done_all);
            } else {
                messageStatusIcon.setImageResource(R.drawable.ic_done);
            }
        }
    }

    // Received Message ViewHolder
    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timeTextView, senderNameTextView;
        ImageView messageImageView;
        MaterialCardView imageCard, messageCard;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            imageCard = itemView.findViewById(R.id.imageCard);
            messageCard = itemView.findViewById(R.id.messageCard);
        }

        public void bind(Message message) {
            if (message.getMessageType().equals("image") && message.getImageUrl() != null) {
                imageCard.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUrl()).into(messageImageView);
            } else {
                imageCard.setVisibility(View.GONE);
            }

            if (message.getMessageText() != null && !message.getMessageText().isEmpty()) {
                messageCard.setVisibility(View.VISIBLE);
                messageTextView.setText(message.getMessageText());
            } else {
                messageCard.setVisibility(View.GONE);
            }

            timeTextView.setText(formatTime(message.getTimestamp()));
        }
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
