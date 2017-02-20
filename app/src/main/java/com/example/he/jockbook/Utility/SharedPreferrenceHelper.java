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

    //保存服务器返回Jock数据的数量
    public static void saveJockSize(Context context, int size) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt("jock_size", size);
        editor.commit();
    }

    public static int getJockSize(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int size = sp.getInt("jock_size", -1);
        return size;
    }

    //保存服务器返回Jock数据的更新时间
    public static void saveJockUpdateTime(Context context, String key,String time) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("jock_time"+key, time);
        editor.commit();
    }

    public static String getJockUpdateTime(Context context,String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String time = sp.getString("jock_time"+key, null);
        return time;
    }

    //保存服务器返回Jock数据的内容
    public static void saveJockContent(Context context, String key,String content) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("jock_content"+key, content);
        editor.commit();
    }

    public static String getJockContent(Context context,String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String time = sp.getString("jock_content"+key, null);
        return time;
    }



}
