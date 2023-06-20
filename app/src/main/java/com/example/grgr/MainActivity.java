package com.example.grgr;

import android.app.ListActivity;
import android.app.usage.UsageStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.Manifest;
import android.content.pm.PackageManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;
import android.view.MenuItem;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.ResolveInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;


import java.util.ArrayList;
import com.example.grgr.AppUsageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {
    private AppUsageTracker appUsageTracker;

    ChatHeadManager chatHeadManager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private FrameLayout mainFrame;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_CODE = 1;

    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MyApp", "ListActivity onCreate called");

//        PermissionNotificationHelper.requestPermission(
//                PERMISSION_REQUEST_CODE,
//                MainActivity.this,
//                Manifest.permission.PACKAGE_USAGE_STATS,
//                "Permission Request",
//                "Please grant the necessary permission for the app to function properly."
//        );

        if (hasPackageUsageStatsPermission()==false){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }


        requestAccessibilityServicePermission();
        requestSystemAlertWindowPermission();
        chatHeadManager = new ChatHeadManager(MainActivity.this);

        log_true_or_false(hasPackageUsageStatsPermission());

        chatHeadManager.showChatHead();

        setContentView(R.layout.activity_main);

        // user
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_first_fragment) {
                    // Handle navigation to first fragment
//                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                    startActivity(intent);
                    selectDrawerItem(1);
                } else if (id == R.id.nav_second_fragment) {
                    // Handle navigation to second fragment
                    Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_third_fragment) {
                    // Handle navigation to second fragment
                    selectDrawerItem(0);
                } else if (id == R.id.nav_forth_fragment){
                    selectDrawerItem(2);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        appUsageTracker = new AppUsageTracker(this);

        // Retrieve the app usage data

        List<UsageStats> usageStatsList = appUsageTracker.getDailyAppUsage();

        // Extract the app names and usage times from the usage stats
        List<AppUsageData> appUsageDataList = new ArrayList<>();
        for (UsageStats usageStats : usageStatsList) {
            String packageName = usageStats.getPackageName();
            String appName = appUsageTracker.getAppName(packageName, this);
            long usageTime = usageStats.getTotalTimeInForeground();
            appUsageDataList.add(new AppUsageData(appName, usageTime));
        }

        // Sort the app usage data list based on usage time in descending order
        Collections.sort(appUsageDataList, new Comparator<AppUsageData>() {
            @Override
            public int compare(AppUsageData appUsageData1, AppUsageData appUsageData2) {
                long usageTime1 = appUsageData1.getUsageTime();
                long usageTime2 = appUsageData2.getUsageTime();
                return Long.compare(usageTime2, usageTime1); // Descending order
            }
        });

        // Create an instance of the custom adapter and set it to the ListView
        List<AppUsageData> appUsageDataSliced = appUsageDataList.subList(0,5);

        DatabaseReference userAppUsageRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("appUsage");
        userAppUsageRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Node cleared successfully
                        // Perform any desired actions
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while clearing the node
                        // Handle the error appropriately
                    }
                });
        for (AppUsageData appUsageData : appUsageDataSliced) {
            String key = userAppUsageRef.push().getKey(); // Generate a unique key
            userAppUsageRef.child(key).setValue(appUsageData)
                    .addOnSuccessListener(aVoid -> {
                        // Data is successfully stored in the database
                    })
                    .addOnFailureListener(e -> {
                        // Failed to store data in the database
                    });
        }

        ListView listView = findViewById(R.id.list_view);
        AppUsageAdapter adapter = new AppUsageAdapter(this, appUsageDataSliced);
        listView.setAdapter(adapter);

//        // Extract the app names and usage times from the usage stats
//        String[] appNames = new String[usageStatsList.size()];
//        long[] usageTimes = new long[usageStatsList.size()];
//
//        for (int i = 0; i < usageStatsList.size(); i++) {
//            UsageStats usageStats = usageStatsList.get(i);
//            String packageName = usageStats.getPackageName();
//            appNames[i] = appUsageTracker.getAppName(packageName);
//            usageTimes[i] = usageStats.getTotalTimeInForeground();
//
//            Log.d("AppUsageTracker", "App Name: " + appNames[i]);
//            Log.d("AppUsageTracker", "Package Name: " + packageName);
//            Log.d("AppUsageTracker", "Usage Time (ms): " + usageTimes[i]);
//        }
//
//        // Create an ArrayAdapter to display the app names in the ListView
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appNames);
//
//        // Set the adapter to the ListView
//        setListAdapter(adapter);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your app logic
                Log.d("AppUsageTracker", "Success");
            } else {
                // Permission denied, show a notification or handle it gracefully
                Log.d("AppUsageTracker", "Fail");
            }
        }
    }

    private boolean hasPackageUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestSystemAlertWindowPermission() {
        if (!hasSystemAlertWindowPermission()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_CODE);
        }
    }
    private boolean hasSystemAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true; // For devices running below Android M, permission is granted by default
    }

    private void requestAccessibilityServicePermission() {
        if (!hasAccessibilityServicePermission()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
    }

    private boolean hasAccessibilityServicePermission() {
        String packageName = getPackageName();
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);

        for (AccessibilityServiceInfo service : enabledServices) {
            if (service.getResolveInfo().serviceInfo.packageName.equals(packageName)) {
                return true;
            }
        }

        return false;
    }

    void log_true_or_false(boolean a) {
        if (a){
            Log.d("AppUsageTracker", "True");
        } else{
            Log.d("AppUsageTracker", "False");
        }
    }

    @Override
    public void onBackPressed() {
        int exitCode = getIntent().getIntExtra("exitCode", 0);
        if (exitCode == ChatHeadManager.EXIT_CODE) {
            Log.d("AppUsageTracker", "Open chat head");
            chatHeadManager.showChatHead();
        }else if ((drawerLayout.isDrawerOpen(GravityCompat.START))) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void selectDrawerItem(int position) {
        // Create a fragment based on the selected drawer item position
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = new FriendsListFragment();
                break;
            case 1:
                fragment = new AccountFragment();
                break;
            case 2:
                fragment = new PostListFragment();
                break;
            // Add more cases for additional drawer items
            default:
                fragment = new FriendsListFragment();
        }

        // Replace the contents of the main frame with the selected fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();

        // Close the navigation drawer
        drawerLayout.closeDrawers();


        // Show a toast or perform any other desired action
        Toast.makeText(this, "Selected item: " + position, Toast.LENGTH_SHORT).show();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }
}