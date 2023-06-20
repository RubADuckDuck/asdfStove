package com.example.grgr;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.database.DatabaseReference;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ListView messageListView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        messageListView = findViewById(R.id.messageListView);

        messageAdapter = new MessageAdapter(this, R.layout.item_message);
        messageListView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage());

        mDatabase.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageAdapter.add(message);
                scrollToBottom();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Handle changes to children here
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Handle child removals here
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // Handle child moves here
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error here
            }
        });
    }

    private void sendMessage() {
        String text = messageEditText.getText().toString().trim();
        if (!text.isEmpty()) {
            String userId = mAuth.getCurrentUser().getUid();
            String messageId = mDatabase.child("messages").push().getKey();
            Message message = new Message(userId, text);
            mDatabase.child("messages").child(messageId).setValue(message);
            messageEditText.setText("");
        }
    }

    private void scrollToBottom() {
        messageListView.post(() -> messageListView.setSelection(messageAdapter.getCount() - 1));
    }
}


