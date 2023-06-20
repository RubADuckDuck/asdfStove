package com.example.grgr;

import android.app.ListActivity;
import android.app.usage.UsageStats;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.util.Log;

import java.util.List;

public class MainActivity extends ListActivity {
    private AppUsageTracker appUsageTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appUsageTracker = new AppUsageTracker(this);

        // Retrieve the app usage data
        List<UsageStats> usageStatsList = appUsageTracker.getDailyAppUsage();

        // Extract the app names and usage times from the usage stats
        String[] appNames = new String[usageStatsList.size()];
        long[] usageTimes = new long[usageStatsList.size()];

        for (int i = 0; i < usageStatsList.size(); i++) {
            UsageStats usageStats = usageStatsList.get(i);
            String packageName = usageStats.getPackageName();
            appNames[i] = appUsageTracker.getAppName(packageName);
            usageTimes[i] = usageStats.getTotalTimeInForeground();

            Log.d("AppUsageTracker", "App Name: " + appNames[i]);
            Log.d("AppUsageTracker", "Package Name: " + packageName);
            Log.d("AppUsageTracker", "Usage Time (ms): " + usageTimes[i]);
        }

        // Create an ArrayAdapter to display the app names and usage times in the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appNames);

        // Set the adapter to the ListView
        setListAdapter(adapter);
    }
}






