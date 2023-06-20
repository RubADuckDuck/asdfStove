package com.example.grgr;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;

public class PostListFragment extends Fragment {

    private DatabaseReference postsRef;

    private PostAdapter adapter;
    private List<Post> postList;
    private Button writePostButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Database reference
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);


        writePostButton = view.findViewById(R.id.write_post_button);
        writePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WritePostActivity.class));
            }
        });


        ListView listView = view.findViewById(R.id.post_list_view);
        postList = new ArrayList<>();
        adapter = new PostAdapter(requireContext(), postList);
        listView.setAdapter(adapter);

        retrieveRecentPosts();

        return view;
    }

    private void retrieveRecentPosts() {
        // Query the posts node to get the 10 most recent posts
        postsRef.limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Retrieve individual post properties from the dataSnapshot
                String username = dataSnapshot.child("username").getValue(String.class);
                String uid = dataSnapshot.child("uid").getValue(String.class);
                String content = dataSnapshot.child("content").getValue(String.class);
                List<String> comments = dataSnapshot.child("comments").getValue(new GenericTypeIndicator<List<String>>() {});

                List<AppUsageData> appUsageDataList = new ArrayList<>();
                DataSnapshot appUsageDataSnapshot = dataSnapshot.child("appUsageDataList");
                for (DataSnapshot childSnapshot : appUsageDataSnapshot.getChildren()) {
                    String appName = childSnapshot.child("appName").getValue(String.class);
                    long usageTime = childSnapshot.child("usageTime").getValue(Long.class);

                    AppUsageData appUsageData = new AppUsageData(appName, usageTime);
                    appUsageDataList.add(appUsageData);
                }

                // Create a new Post instance with the retrieved properties
                Post post = new Post(username, uid, content, comments, appUsageDataList);

                // Add the post to the list and notify the adapter
                postList.add(post);
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            // Other ChildEventListener methods
            // onChildChanged(), onChildRemoved(), onChildMoved(), onCancelled()
            // You can handle these methods based on your requirements

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur
            }
        });
    }
}