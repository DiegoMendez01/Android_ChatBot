package com.example.androidchatbot.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidchatbot.Activities.ChatActivity;
import com.example.androidchatbot.Models.Messages;
import com.example.androidchatbot.R;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private LinkedList<Messages> messages;
    private final Context context;

    public MessagesAdapter(LinkedList<Messages> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_messages_adapter, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Messages messageList = messages.get(position);
        if(!messageList.getProfilePicture().isEmpty()){
            Picasso.get().load(messageList.getProfilePicture()).into(holder.profilePicture);
        }

        holder.username.setText(messageList.getUsername());
        holder.lastMessage.setText(messageList.getLastMessage());

        if(messageList.getUnseenMessages() == 0){
            holder.unseenMessage.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));
        }else{
            holder.unseenMessage.setVisibility(View.VISIBLE);
            holder.unseenMessage.setText(messageList.getUnseenMessages()+"");
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.theme_color_80));
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChat(messageList);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(LinkedList<Messages> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profilePicture;
        private TextView username, lastMessage, unseenMessage;
        private LinearLayout rootLayout;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.profilePicture);
            username       = itemView.findViewById(R.id.username);
            lastMessage    = itemView.findViewById(R.id.lastMessage);
            unseenMessage  = itemView.findViewById(R.id.unseenMessages);
            rootLayout     = itemView.findViewById(R.id.rootLayout);
        }
    }
    // Method to go Chat
    private void onClickChat(Messages messagesList)
    {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("phone", messagesList.getPhone());
        intent.putExtra("username", messagesList.getUsername());
        intent.putExtra("profile_picture", messagesList.getProfilePicture());
        intent.putExtra("chat_key", messagesList.getChatKey());
        context.startActivity(intent);
    }
}
