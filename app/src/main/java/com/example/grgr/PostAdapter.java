package com.example.grgr;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.grgr.AppUsageData;
import com.example.grgr.Post;
import com.example.grgr.R;

import java.util.List;
import java.util.Locale;

public class PostAdapter extends ArrayAdapter<Post> {

    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        super(context, 0, postList);
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.post_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.usernameTextView = view.findViewById(R.id.username_text_view);
            viewHolder.contentTextView = view.findViewById(R.id.content_text_view);
            viewHolder.commentsTextView = view.findViewById(R.id.comments_text_view);
            viewHolder.usageTimeTextView = view.findViewById(R.id.usage_time_text_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Post post = postList.get(position);

        viewHolder.usernameTextView.setText(post.getUsername());
        viewHolder.contentTextView.setText(post.getContent());

//        int numComments = post.getComments() != null ? post.getComments().size() : 0;
//        viewHolder.commentsTextView.setText(context.getResources().getQuantityString(R.plurals.comments_count, numComments, numComments));
        viewHolder.usageTimeTextView.setText(formatUsageTime(post.getAppUsageDataList()));

        return view;
    }

    private String formatUsageTime(List<AppUsageData> appUsageDataList) {
        StringBuilder usageDataBuilder = new StringBuilder();
        if (appUsageDataList != null && !appUsageDataList.isEmpty()) {
            for (AppUsageData appUsageData : appUsageDataList) {
                String appName = appUsageData.getAppName();
                long usageTime = appUsageData.getUsageTime();
                String formattedTime = formatTime(usageTime);

                // Append the app usage data to the builder
                usageDataBuilder.append(appName).append(": ").append(formattedTime).append("\n");
            }
        }

        return usageDataBuilder.toString().trim();
    }

    private String formatTime(long totalUsageTime) {
        long hours = totalUsageTime / 3600;
        long minutes = (totalUsageTime % 3600) / 60;
        long seconds = totalUsageTime % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static class ViewHolder {
        TextView usernameTextView;
        TextView contentTextView;
        TextView commentsTextView;
        TextView usageTimeTextView;
    }
}
