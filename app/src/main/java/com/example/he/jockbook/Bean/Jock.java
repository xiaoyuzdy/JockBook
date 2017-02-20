package com.example.he.jockbook.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * GSON解析笑话JSON数据对应的实体类
 * Created by he on 2017/2/18.
 */

public class Jock {
    public String error_code;
    public String reason;
    @SerializedName("result")
    public JockResult result;

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
