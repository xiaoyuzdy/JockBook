package com.example.he.jockbook.Utility;

import android.text.TextUtils;
import android.util.Log;

import com.example.he.jockbook.Bean.Jock;
import com.example.he.jockbook.Bean.JockData;
import com.google.gson.Gson;

/**
 * Created by he on 2017/2/18.
 */

public class Utility {

    private static final String TAG="Utility";

    /**
     * 解析和处理服务器返回的笑话数据
     * @param jsonData
     * @return
     */
    public  static void handleJock(String jsonData){
        if(!TextUtils.isEmpty(jsonData)){
            Gson gson=new Gson();
            Jock jock=gson.fromJson(jsonData,Jock.class);
            Log.d(TAG, "error_code"+jock.getError_code());
            Log.d(TAG, "reason "+jock.getReason());
            for(JockData d:jock.result.jockDatas){
                Log.d(TAG, "handleJock: "+d.content);
            }

        }
    }
}