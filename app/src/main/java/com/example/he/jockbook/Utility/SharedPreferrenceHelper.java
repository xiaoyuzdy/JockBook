package com.example.he.jockbook.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by he on 2017/1/30.
 */

public class SharedPreferrenceHelper {


    //保存必应每日一图
    public static void saveBingPicUrl(Context context, String urlImage) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("bing_pic", urlImage);
        editor.commit();
    }

    public static String getBingPicUrl(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String url = sp.getString("bing_pic", null);
        return url;
    }


}
