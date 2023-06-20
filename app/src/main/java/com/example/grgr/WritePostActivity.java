package com.example.grgr;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class WritePostActivity extends AppCompatActivity {

    private EditText contentEditText;
    private Button sendButton;

    private DatabaseReference postsRef;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        // Initialize Firebase Database reference
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        contentEditText = findViewById(R.id.content_edit_text);
        sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentEditText.getText().toString().trim();
                if (!content.isEmpty()) {
                    sendPost(content);
                } else {
                    Toast.makeText(WritePostActivity.this, "Please enter a post", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPost(String content) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {

                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Log.e("firebase", "Error getting data", task.getException());

                    DataSnapshot dataSnapshot = task.getResult();
                    DataSnapshot usernameSnapshot = dataSnapshot.child("username");
                    String username = usernameSnapshot.getValue(String.class);
                    DataSnapshot appUsageSnapshot = dataSnapshot.child("appUsage");


                    List<AppUsageData> appUsageDataList = new ArrayList<>();
                    for (DataSnapshot childSnapshot : appUsageSnapshot.getChildren()) {
                        String appName = childSnapshot.child("appName").getValue(String.class);
                        long usageTime = childSnapshot.child("usageTime").getValue(Long.class);

                        AppUsageData appUsageData = new AppUsageData(appName, usageTime);
                        appUsageDataList.add(appUsageData);
                    }

                    Post post = new Post(username, uid, content, null, appUsageDataList);
                    postsRef.push().setValue(post)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(WritePostActivity.this, "Post sent successfully", Toast.LENGTH_SHORT).show();
                                contentEditText.setText("");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(WritePostActivity.this, "Failed to send post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });


    }
}
