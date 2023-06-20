package com.example.grgr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppUsageAdapter extends ArrayAdapter<AppUsageData> {

    private LayoutInflater inflater;

    public AppUsageAdapter(Context context, List<AppUsageData> appUsageDataList) {
        super(context, 0, appUsageDataList);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_app_usage, parent, false);

            holder = new ViewHolder();
            holder.appNameTextView = convertView.findViewById(R.id.appNameTextView);
            holder.usageTimeTextView = convertView.findViewById(R.id.usageTimeTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppUsageData appUsageData = getItem(position);
        holder.appNameTextView.setText(appUsageData.getAppName());
        holder.usageTimeTextView.setText(formatUsageTime(appUsageData.getUsageTime()));

        return convertView;
    }

    private String formatUsageTime(long usageTimeInMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(usageTimeInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(usageTimeInMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(usageTimeInMillis) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static class ViewHolder {
        TextView appNameTextView;
        TextView usageTimeTextView;
    }
}