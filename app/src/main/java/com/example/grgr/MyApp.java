package com.example.grgr;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;


public class MyApp extends Application {
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_CODE = 1;

    @Override
    public void onCreate() {
        super.onCreate();


        // Perform any initialization or setup code here
        Log.d("MyApp", "Application onCreate called");

        // You can add more code specific to your application here

    }


}
