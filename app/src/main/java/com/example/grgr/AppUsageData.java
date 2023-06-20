package com.example.grgr;

public class AppUsageData {
    private String appName;
    private long usageTime;

    public AppUsageData(String appName, long usageTime) {
        this.appName = appName;
        this.usageTime = usageTime;
    }

    public String getAppName() {
        return appName;
    }

    public long getUsageTime() {
        return usageTime;
    }
}
