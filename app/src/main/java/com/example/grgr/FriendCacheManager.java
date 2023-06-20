package com.example.grgr;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendCacheManager {
    private static List<Friend> friendList;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    public static void updateFollowerList(String myUid){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference followerRef =  mDatabase.child("User").child(myUid).child("follow");

        followerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    String uid = itemSnapshot.getKey();
                    String name = itemSnapshot.getValue(String.class);
                    System.out.println(uid);
                    System.out.println(name);
                    friendList.add(new Friend(name, uid));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static List<Friend> getFriends() {
        return friendList;
    }
}

