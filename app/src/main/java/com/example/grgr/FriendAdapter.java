package com.example.grgr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.grgr.Friend;
import com.example.grgr.R;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<Friend> {

    private LayoutInflater inflater;

    public FriendAdapter(Context context, List<Friend> friendsList) {
        super(context, 0, friendsList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.friend_item_layout, parent, false);
        }

        // Get the Friend object at the current position
        Friend friend = getItem(position);

        // Set the name of the Friend to the TextView in the layout
        TextView nameTextView = itemView.findViewById(R.id.friend_name_text_view);
        nameTextView.setText(friend.getName());

        return itemView;
    }
}