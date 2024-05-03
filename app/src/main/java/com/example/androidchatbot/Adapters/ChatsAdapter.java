package com.example.androidchatbot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidchatbot.Models.Chats;
import com.example.androidchatbot.R;
import com.example.androidchatbot.utils.MemoryData;

import java.util.LinkedList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyHolder> {

    private LinkedList<Chats> chats;
    private final Context context;
    private String userPhone;

    public ChatsAdapter(LinkedList<Chats> chats, Context context) {
        this.chats   = chats;
        this.context = context;
        this.userPhone = MemoryData.getData(context);
    }

    public void updateChat(LinkedList<Chats> chats){
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_chats_adapter, null);
        return new ChatsAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.MyHolder holder, int position) {
        Chats chatsList = chats.get(position);

        if(chatsList.getPhone().equals(userPhone)) {
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.oppoLayout.setVisibility(View.GONE);

            holder.myMessage.setText(chatsList.getMessage());
            holder.myMsgTime.setText(chatsList.getDate()+" "+chatsList.getTime());
        }else{
            holder.myLayout.setVisibility(View.GONE);
            holder.oppoLayout.setVisibility(View.VISIBLE);

            holder.oppoMessage.setText(chatsList.getMessage());
            holder.oppoMessageTime.setText(chatsList.getDate()+" "+chatsList.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private LinearLayout oppoLayout, myLayout;
        private TextView oppoMessage, myMessage, oppoMessageTime, myMsgTime;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            oppoLayout      = itemView.findViewById(R.id.oppoLayout);
            myLayout        = itemView.findViewById(R.id.myLayout);
            oppoMessage     = itemView.findViewById(R.id.oppoMessage);
            myMessage       = itemView.findViewById(R.id.myMessage);
            oppoMessageTime = itemView.findViewById(R.id.oppoMessageTime);
            myMsgTime       = itemView.findViewById(R.id.myMsgTime);
        }
    }
}
