package com.example.grgr;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.content.pm.ApplicationInfo;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.widget.TextView;

public class ChatHeadManager extends AccessibilityService {
    public static final int EXIT_CODE = 222; // Custom exit code
    private final int DRAG_THRESHOLD = 800; // Threshold for closing the chat head window

    private final Activity activity;
    private final WindowManager windowManager;
    private View chatHeadView;
    private WindowManager.LayoutParams chatHeadParams;
    private TextView appNameTextView;

    private float initialX;
    private float initialY;
    private float initialTouchX;
    private float initialTouchY;

    final String[] textList = { "폰 볼 시간이 있냐? 정신 나갔네 ㅋㅋ", "쿠키런이 입에 들어가?", "오늘 유투브만 3시간이다. 좀 꺼라;;" };
    int currentTextIndex = 0;

    public ChatHeadManager(Activity activity) {
        this.activity = activity;
        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Check if the event is a window state change
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // Get the package name of the currently launched app
            String packageName = event.getPackageName().toString();

            // Update the app name in the chat head
            updateAppName(packageName);
        }
    }

    @Override
    public void onInterrupt() {
    }

    public void showChatHead() {
//        chatHeadView = new ImageView(activity);
//        chatHeadView.setImageResource(R.drawable.ic_launcher_foreground);

        chatHeadView = LayoutInflater.from(activity).inflate(R.layout.layout_chat_head, null);

        chatHeadParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        chatHeadParams.gravity = Gravity.TOP | Gravity.LEFT;
        chatHeadParams.x = 0;
        chatHeadParams.y = 100;

        chatHeadView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = chatHeadParams.x;
                        initialY = chatHeadParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        chatHeadParams.x = (int) (initialX + (event.getRawX() - initialTouchX));
                        chatHeadParams.y = (int) (initialY + (event.getRawY() - initialTouchY));
                        windowManager.updateViewLayout(chatHeadView, chatHeadParams);

                        // Check if the chat head is dragged below the threshold
                        if (chatHeadParams.y - initialY > DRAG_THRESHOLD) {
                            removeChatHead(); // Remove the chat head window
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Rotate to the next text
                        currentTextIndex = (currentTextIndex + 1) % textList.length;
                        String nextText = textList[currentTextIndex];
                        appNameTextView.setText(nextText);
                        return true;
                    default:
                        return false;
                }
            }
        });

        appNameTextView = chatHeadView.findViewById(R.id.appNameTextView);

        windowManager.addView(chatHeadView, chatHeadParams);
    }

    public void removeChatHead() {
        if (chatHeadView != null) {
            windowManager.removeView(chatHeadView);
            chatHeadView = null;
        }
    }

    public void exitApp() {
        activity.finish();
        activity.overridePendingTransition(0, 0);
        System.exit(EXIT_CODE);
    }

    private void updateAppName(String packageName) {
        // Get the application name from the package name
        PackageManager packageManager = activity.getPackageManager();
        String appName = "";

        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            appName = (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Set the app name in the text view
        appNameTextView.setText(appName);
    }
}
