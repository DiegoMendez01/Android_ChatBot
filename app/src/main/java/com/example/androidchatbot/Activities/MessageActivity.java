package com.example.androidchatbot.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.androidchatbot.Adapters.MessagesAdapter;
import com.example.androidchatbot.MainActivity;
import com.example.androidchatbot.Models.Messages;
import com.example.androidchatbot.utils.MemoryData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.example.androidchatbot.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private LinkedList<Messages> messagesList = new LinkedList<>();
    private DatabaseReference mDatabase;
    private boolean dataSet = false;
    private String password, phone, username, lastMessages = "", chatKey = "";
    private int unseenMessages = 0;
    private CircleImageView profileUser;
    private RecyclerView messagesRecyclerView;

    private MessagesAdapter messagesAdapter;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        profileUser          = findViewById(R.id.profileUser);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);

        // Get putExtra
        phone    = getIntent().getStringExtra("phone");
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set adapter to recyclerview
        messagesAdapter = new MessagesAdapter(messagesList, MessageActivity.this);
        messagesRecyclerView.setAdapter(messagesAdapter);

        // Dialog Picasso
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        profileUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicMenuView(v);
            }
        });

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profilePictureURL = snapshot.child("users").child(phone).child("profile_picture").getValue(String.class);

                if(!profilePictureURL.isEmpty()) {
                    // Set image picture
                    Picasso.get().load(profilePictureURL).into(profileUser);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();

                for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {
                    final String getPhone = dataSnapshot.getKey();
                    final String getUserName = dataSnapshot.child("username").getValue(String.class);
                    final String getProfilePicture = dataSnapshot.child("profile_picture").getValue(String.class);

                    if (!getPhone.equals(phone)) {
                        boolean chatFound = false;

                        for (DataSnapshot chatSnapshot : snapshot.child("chats").getChildren()) {
                            final String user1 = chatSnapshot.child("user_1").getValue(String.class);
                            final String user2 = chatSnapshot.child("user_2").getValue(String.class);

                            if ((user1.equals(getPhone) && user2.equals(phone)) || (user1.equals(phone) && user2.equals(getPhone))) {
                                // Chat exists for the two users
                                String chatKey = chatSnapshot.getKey();
                                String lastMessage = "";
                                int unseenMessagesCount = 0;

                                for (DataSnapshot messageSnapshot : chatSnapshot.child("messages").getChildren()) {
                                    long messageKey = Long.parseLong(messageSnapshot.getKey());
                                    long lastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(MessageActivity.this, chatKey));

                                    if (messageKey > lastSeenMessage) {
                                        unseenMessagesCount++;
                                    }

                                    lastMessage = messageSnapshot.child("msg").getValue(String.class);
                                }

                                Messages messages = new Messages(getUserName, getPhone, lastMessage, getProfilePicture, unseenMessagesCount, chatKey);
                                messagesList.add(messages);
                                chatFound = true;
                                break; // Salir del bucle una vez que se encuentre el chat
                            }
                        }

                        if (!chatFound) {
                            // Si no se encuentra un chat para el usuario, agregar un usuario sin chat
                            Messages messages = new Messages(getUserName, getPhone, "", getProfilePicture, 0, "");
                            messagesList.add(messages);
                        }
                    }
                }

                // Actualizar el adaptador con los nuevos datos
                messagesAdapter.updateData(messagesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de la base de datos
            }
        });
    }
    // Llamar al menuView
    private void onClicMenuView(View view)
    {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_profile);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int idItem = item.getItemId();
                if (idItem == R.id.itemCerrarSesion) {
                    Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        popupMenu.show();
    }
}