package com.example.he.jockbook.Utility;

import android.text.TextUtils;
import android.util.Log;

import com.example.he.jockbook.Bean.Image;
import com.example.he.jockbook.Bean.ImageData;
import com.example.he.jockbook.Bean.Jock;
import com.example.he.jockbook.Bean.JockData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by he on 2017/2/18.
 */

public class Utility {

    private static final String TAG = "Utility";

//    /**
//     * 解析和处理服务器返回的笑话数据
//     *
//     * @param jsonData
//     * @return
//     */
//    public static synchronized void handleJock(String jsonData) {
//        if (!TextUtils.isEmpty(jsonData)) {
//            Gson gson = new Gson();
//            Jock jock = gson.fromJson(jsonData, Jock.class);
//            Log.d(TAG, "error_code" + jock.getError_code());
//            Log.d(TAG, "reason " + jock.getReason());
//            if (jock.getReason().equals("Success")) {
//                //保存数据
//                int i = 0;
//                for (JockData d : jock.result.jockDatas) {
//                    SharedPreferrenceHelper.saveJockUpdateTime(myApplication.getContext(), "" + i, d.updatetime);
//                    SharedPreferrenceHelper.saveJockContent(myApplication.getContext(), "" + i, d.content);
//                    i++;
//                    Log.d(TAG, "handleJock: " + d.content);
//                }
//                SharedPreferrenceHelper.saveJockSize(myApplication.getContext(), i);
//            }
//        }
//    }


    /**
     * 解析和处理服务器返回的笑话数据
     *
     * @param jsonData
     * @return
     */
    public static synchronized List<JockData> handleJock(String jsonData) {
        List<JockData> dataList = new ArrayList<>();
        if (!TextUtils.isEmpty(jsonData)) {
            try {
                //用于判断是否超过连接次数
                JSONObject object = new JSONObject(jsonData);
                String error = object.getString("error_code");
                Log.d(TAG, "handleJock: error_code" + error);
                if (error.equals("10012")) {
                    Log.d(TAG, "今日连接次数已用完，无法连接服务器");
                }
                //获取数据成功
                else if (error.equals("0")) {

                    Gson gson = new Gson();
                    if (isAvailableJockDatas(jsonData)) {
                        Jock jock = gson.fromJson(jsonData, Jock.class);
                        Log.d(TAG, "error_code" + jock.getError_code());
                        Log.d(TAG, "reason " + jock.getReason());
                        if (jock.getReason().equals("Success")) {
                            for (JockData data : jock.result.jockDatas) {
                                JockData d = new JockData();
                                d.updatetime = data.updatetime;
                                d.content = data.content;
                                dataList.add(d);
                            }
                        }
                    }

                } else {
                    Log.d(TAG, "连接服务器失败");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }


    /**
     * 解析和处理服务器返回的趣图数据
     *
     * @param imageData
     */
    public static synchronized List<ImageData> handleImage(String imageData) {
        List<ImageData> dataList = new ArrayList<>();
        if (!TextUtils.isEmpty(imageData)) {
            try {
                //用于判断是否超过连接次数
                JSONObject object = new JSONObject(imageData);
                String error = object.getString("error_code");
                Log.d(TAG, "handleJock: error_code" + error);
                if (error.equals("10012")) {
                    Log.d(TAG, "今日连接次数已用完，无法连接服务器");
                }
                //获取数据成功
                else if (error.equals("0")) {
                    if (isAvailableImageDatas(imageData)) {
                        Gson gson = new Gson();
                        Image image = gson.fromJson(imageData, Image.class);
                        if (image.getReason().equals("Success")) {

                            for (ImageData t : image.getResult().imageDatas) {

                                ImageData data = new ImageData();
                                data.content = t.content;
                                data.updatetime = t.updatetime;
                                data.url = t.url;
                                dataList.add(data);
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "连接服务器失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return dataList;
    }

    private static boolean isAvailableJockDatas(String jockDatas) {
        try {
            Gson gson = new Gson();
            gson.fromJson(jockDatas, Jock.class);
            return true;
        } catch (JsonSyntaxException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private static boolean isAvailableImageDatas(String imageDatas) {
        try {
            Gson gson = new Gson();
            gson.fromJson(imageDatas, Image.class);
            return true;
        } catch (JsonSyntaxException exception) {
            exception.printStackTrace();
            return false;
        }
    }


}
