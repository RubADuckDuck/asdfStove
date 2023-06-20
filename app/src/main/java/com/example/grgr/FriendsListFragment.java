package com.example.grgr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsListFragment extends Fragment {

    private ListView friendsListView;
    private ArrayAdapter<Friend> friendsAdapter;

    private List<Friend> friendsList;
    private FriendAdapter friendAdapter;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        friendsList = new ArrayList<Friend>();

        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        FriendCacheManager.updateFollowerList(mAuth.getCurrentUser().getUid());


        friendsListView = rootView.findViewById(R.id.friendsListView);
        String myUid = mAuth.getUid();

        DatabaseReference followRef =  mDatabase.child("users").child(myUid).child("follow");

        followRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnap = task.getResult();
            }
        });

        followRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    String uid = itemSnapshot.getKey();
                    String name = itemSnapshot.getValue(String.class);
                    System.out.println(uid);
                    System.out.println(name);
                    friendsList.add(new Friend(name, uid));
                }
                if (friendsList != null) {
                    friendsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, friendsList);
                    friendAdapter = new FriendAdapter(requireContext(), friendsList);

                    friendsListView.setAdapter(friendsAdapter);
                    friendsListView.setAdapter(friendAdapter);

                    friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            Friend selectedFriend = friendsList.get(position);
                            openChatBox(selectedFriend);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return rootView;
    }


    private void openChatBox(Friend friend) {
        Toast.makeText(requireContext(), "Opening chat with " + friend.getName(), Toast.LENGTH_SHORT).show();
        // Perform actions to open the chat box for the selected friend
        // You can start a new activity or fragment for the chat box here
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        startActivity(intent);
    }
}

