package com.example.he.jockbook.Bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by he on 2017/2/22.
 */

public class Image {
    public String error_code;
    public String reason;
    @SerializedName("result")
    public ImageResult result;

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

    public ImageResult getResult() {
        return result;
    }

    public void setResult(ImageResult result) {
        this.result = result;
    }
}
