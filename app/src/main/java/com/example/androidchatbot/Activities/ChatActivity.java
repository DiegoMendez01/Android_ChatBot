package com.example.androidchatbot.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidchatbot.Adapters.ChatsAdapter;
import com.example.androidchatbot.Adapters.MessagesAdapter;
import com.example.androidchatbot.Models.Chats;
import com.example.androidchatbot.R;
import com.example.androidchatbot.utils.MemoryData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.google.firebase.database.DatabaseReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private final LinkedList<Chats> chats = new LinkedList<>();
    private DatabaseReference mDatabase;
    private ChatsAdapter chatsAdapter;
    private RecyclerView chattingRecyclerView;
    private String getChatKey;
    private String getPhoneUser = "";
    private boolean loadingFirstTime = true;
    // ImageView´s
    ImageView backBtn, sendBtn;
    // EditText´s
    EditText edMessage;
    // TextView´s
    TextView usernameChat;
    // CircleImageView´s
    CircleImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        backBtn              = findViewById(R.id.backBtn);
        sendBtn              = findViewById(R.id.sendBtn);
        edMessage            = findViewById(R.id.edMessage);
        usernameChat         = findViewById(R.id.usernameChat);
        profilePicture       = findViewById(R.id.profilePicture);
        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        // Get data from messagesAdapter
        final String getPhone          = getIntent().getStringExtra("phone");
        final String getUsername       = getIntent().getStringExtra("username");
        final String getProfilePicture = getIntent().getStringExtra("profile_picture");
        getChatKey        = getIntent().getStringExtra("chat_key");

        usernameChat.setText(getUsername);
        getPhoneUser = MemoryData.getData(ChatActivity.this);
        // Cargar imagen predeterminada si getProfilePicture es nulo o vacío
        if (!getProfilePicture.isEmpty()) {
            Picasso.get().load(getProfilePicture).into(profilePicture);
        } else {
            // Cargar una imagen predeterminada si no hay ninguna proporcionada
            profilePicture.setImageResource(R.drawable.user_icon);
        }

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        chatsAdapter = new ChatsAdapter(chats, ChatActivity.this);
        chattingRecyclerView.setAdapter(chatsAdapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendBtn(getPhone, getPhoneUser, getUsername);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadChatMessages(getUsername);

    }

    private void onClickSendBtn(String getPhone, String getPhoneUser, String getUsername)
    {
        final String getMessageTxt = edMessage.getText().toString();

        // Validate message text
        if (!getMessageTxt.isEmpty()) {
            // Get current Timestamp
            final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);

            // Check if chat exists for the two users
            mDatabase.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean chatExists = false;
                    for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                        String user1 = chatSnapshot.child("user_1").getValue(String.class);
                        String user2 = chatSnapshot.child("user_2").getValue(String.class);
                        if ((user1.equals(getPhoneUser) && user2.equals(getPhone)) || (user1.equals(getPhone) && user2.equals(getPhoneUser))) {
                            // Chat exists
                            chatExists = true;
                            getChatKey = chatSnapshot.getKey();
                            break;
                        }
                    }

                    // If chat does not exist, generate a new chatKey
                    if (!chatExists) {
                        getChatKey = String.valueOf(snapshot.getChildrenCount() + 1);
                        // Save new chat details to the database
                        mDatabase.child("chats").child(getChatKey).child("user_1").setValue(getPhoneUser);
                        mDatabase.child("chats").child(getChatKey).child("user_2").setValue(getPhone);
                    }

                    // Save message to database
                    mDatabase.child("chats").child(getChatKey).child("messages").child(currentTimestamp).child("msg").setValue(getMessageTxt);
                    mDatabase.child("chats").child(getChatKey).child("messages").child(currentTimestamp).child("phone").setValue(getPhoneUser);

                    // Clear message input
                    edMessage.setText("");
                    loadChatMessages(getUsername);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

    private void loadChatMessages(String getUsername){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(getChatKey.isEmpty()){
                    // Generate chatKey by Default ChatKey is 1
                    getChatKey = "1";
                    if(snapshot.hasChild("chats")){
                        getChatKey = String.valueOf(snapshot.child("chats").getChildrenCount() + 1);
                    }
                }

                if(snapshot.hasChild("chats")){
                    if(snapshot.child("chats").child(getChatKey).hasChild("messages")){
                        chats.clear();
                        for(DataSnapshot messagesSnapshot : snapshot.child("chats").child(getChatKey).child("messages").getChildren()){
                            if(messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("phone")){
                                final String messageTimestamps = messagesSnapshot.getKey();
                                final String getPhone = messagesSnapshot.child("phone").getValue(String.class);
                                final String getMsg   = messagesSnapshot.child("msg").getValue(String.class);

                                Timestamp timestamp = new Timestamp(Long.parseLong(messageTimestamps));
                                Date date = new Date(timestamp.getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
                                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

                                Chats chatList = new Chats(getPhone, getUsername, getMsg, simpleDateFormat.format(date), simpleTimeFormat.format(date));
                                chats.add(chatList);

                                if(loadingFirstTime || Long.parseLong(messageTimestamps) > Long.parseLong(MemoryData.getLastMsgTS(ChatActivity.this, getChatKey))){
                                    loadingFirstTime = false;
                                    // Save last message timestamp
                                    MemoryData.saveLastMsgTS(messageTimestamps, getChatKey, ChatActivity.this);
                                    chatsAdapter.updateChat(chats);
                                    chattingRecyclerView.scrollToPosition(chats.size() - 1);
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}