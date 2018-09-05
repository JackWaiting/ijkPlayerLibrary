package com.platomix.lib.playerengine.videoplayer.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * Window工具类
 * Created by JackWaiting on 2017/4/10.
 */

public class WindowUtil {

    public static int getNavigationBarHeight(Context context) {
        if (!hasNavigationBar(context)) {
            return 0;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId);
    }

    public static boolean hasNavigationBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager(context).getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.x != size.x || realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context, boolean isIncludeNav) {
        if (isIncludeNav) {
            return context.getResources().getDisplayMetrics().heightPixels + getNavigationBarHeight(context);
        } else {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
    }

    /**
     * 隐藏ActionBar，StatusBar，NavigationBar
     * @param context 上下文
     */
    @SuppressLint("RestrictedApi")
    public static void hideSystemBar(Context context) {
        AppCompatActivity appCompatActivity = getAppCompActivity(context);
        if (appCompatActivity != null) {
            ActionBar ab = appCompatActivity.getSupportActionBar();
            if (ab != null && ab.isShowing()) {
                ab.setShowHideAnimationEnabled(false);
                ab.hide();
            }
        }
        hideNavigationBar(context);
    }

    @SuppressLint("RestrictedApi")
    public static void showSystemBar(final Context context) {
        showNavigationBar(context);
        AppCompatActivity appCompatActivity = getAppCompActivity(context);
        if (appCompatActivity != null) {
            ActionBar ab = appCompatActivity.getSupportActionBar();
            if (ab != null && !ab.isShowing()) {
                ab.setShowHideAnimationEnabled(false);
                ab.show();
            }
        }
    }

    public static Activity scanForActivity(Context context) {
        return context == null ? null : (context instanceof Activity ? (Activity) context : (context instanceof ContextWrapper ? scanForActivity(((ContextWrapper) context).getBaseContext()) : null));
    }

    private static void hideNavigationBar(Context context) {
        View decorView = scanForActivity(context).getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(flags);
    }

    private static void showNavigationBar(Context context) {
        View decorView = scanForActivity(context).getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        systemUiVisibility &= ~flags;
        decorView.setSystemUiVisibility(systemUiVisibility);
    }


    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static boolean isEdge(Context context, MotionEvent e) {
        int edgeSize = dp2px(context, 50);
        return e.getRawX() < edgeSize
                || e.getRawX() > getScreenWidth(context) - edgeSize
                || e.getRawY() < edgeSize
                || e.getRawY() > getScreenHeight(context, true) - edgeSize;
    }
}
