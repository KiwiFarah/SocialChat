package uk.ac.wlv.socialchat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private List<Message> messagesFull;

    public MessageAdapter(List<Message> messages) {
        if (messages != null) {
            this.messages = messages;
            this.messagesFull = new ArrayList<>(messages);
        } else {
            this.messages = new ArrayList<>();
            this.messagesFull = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageTextView.setText(message.getMessage());

        if (message.getImageUri() != null && !message.getImageUri().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(message.getImageUri()))
                    .into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        // Change the background color based on selection
        int lightRed = Color.parseColor("#FFCCCB");
        holder.itemView.setBackgroundColor(message.isSelected() ? lightRed : Color.TRANSPARENT);

        // OnClickListener for viewing the message detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Message clickedMessage = messages.get(adapterPosition);
                    Intent intent = new Intent(v.getContext(), MessageDetailActivity.class);
                    intent.putExtra("message_id", clickedMessage.getId());
                    v.getContext().startActivity(intent);
                }
            }
        });

        // OnLongClickListener for selecting the message
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    message.setSelected(!message.isSelected());
                    notifyItemChanged(adapterPosition);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.d("MessageAdapter", "Item count: " + messages.size());
        return messages.size();
    }
    public void updateMessages(List<Message> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        messagesFull.clear();
        messagesFull.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        text = text.toLowerCase();
        List<Message> filteredMessages = new ArrayList<>(); // Initialize as new ArrayList

        if (text.isEmpty()) {
            filteredMessages.addAll(messagesFull);
        } else {
            for (Message message : messagesFull) {
                if (message.getMessage().toLowerCase().contains(text)) {
                    filteredMessages.add(message);
                }
            }
        }

        Log.d("MessageAdapter", "Filtered messages count: " + filteredMessages.size());

        // Update the list of messages and notify the adapter
        messages.clear();
        messages.addAll(filteredMessages);
        notifyDataSetChanged();
    }

    public List<Message> getSelectedMessages() {
        List<Message> selectedMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.isSelected()) {
                selectedMessages.add(message);
            }
        }
        return selectedMessages;
    }


    // ViewHolder class
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView imageView; // ImageView to display the image

        ViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
