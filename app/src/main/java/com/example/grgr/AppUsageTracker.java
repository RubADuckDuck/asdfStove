package com.example.grgr;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

public class AppUsageTracker {
    private Context context;
    private UsageStatsManager usageStatsManager;

    public AppUsageTracker(Context context) {
        this.context = context;
        this.usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public List<UsageStats> getDailyAppUsage() {
        Log.d("AppUsageTracker", "retrieving data");
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        );

        int count = usageStatsList.size();
        Log.d("AppUsageTracker", "Number of UsageStats: " + count);
        return usageStatsList;
    }

    public String getAppName(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        String appName;
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            appName = packageName;
            e.printStackTrace();
        }
        return appName;
    }
}