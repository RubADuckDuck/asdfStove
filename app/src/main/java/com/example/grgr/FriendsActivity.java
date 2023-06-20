package com.example.grgr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private Button addFriendButton;
    private ListView friendsListView;

    private List<String> friends;
    private ArrayAdapter<String> adapter;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    public void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            FriendCacheManager.updateFollowerList(currentUser.getUid());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        usernameEditText = findViewById(R.id.usernameEditText);
        addFriendButton = findViewById(R.id.addFriendButton);
        friendsListView = findViewById(R.id.friendsListView);

        friends = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friends);
        friendsListView.setAdapter(adapter);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                if (!username.isEmpty()) {
                    addFriend(username);
                    usernameEditText.setText("");
                } else {
                    Toast.makeText(FriendsActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addFriend(String username) {
        // Here you would typically interact with your FriendRepository to add the friend
        // For simplicity, we're just adding the username to the list and updating the ListView
        friends.add(username);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String myUid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String targetUid;

        DatabaseReference uidRef = mDatabase.child("username2uid").child(username);

        uidRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid = snapshot.getValue(String.class);
                mDatabase.child("users").child(myUid).child("follow").child(uid).setValue(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter.notifyDataSetChanged();
    }
}
