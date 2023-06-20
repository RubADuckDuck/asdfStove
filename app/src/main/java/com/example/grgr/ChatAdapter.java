package com.example.grgr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> messages;

    public ChatAdapter(Context context, List<String> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String message = messages.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        }

        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        return convertView;
    }
}
