package com.example.he.jockbook.Utility;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

/**
 * 5.0以上系统隐藏状态栏
 * Created by he on 2017/1/25.
 */

public class HideStatusBar {
    public static void hide(Activity activity){
        if (Build.VERSION.SDK_INT>=21){
            View decorView=activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
