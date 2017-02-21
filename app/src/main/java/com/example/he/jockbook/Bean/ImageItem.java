package com.example.he.jockbook.Bean;

/**
 * Created by he on 2017/2/20.
 */

public class ImageItem {
    private String mUpdateTime;
    private String mImageTitle;
    private String mImageUrl;

    public ImageItem(String mImageUrl, String mImageTitle, String mUpdateTime) {
        this.mImageUrl = mImageUrl;
        this.mUpdateTime = mUpdateTime;
        this.mImageTitle = mImageTitle;
    }

    public String getmImageTitle() {
        return mImageTitle;
    }

    public void setmImageTitle(String mImageTitle) {
        this.mImageTitle = mImageTitle;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmUpdateTime() {
        return mUpdateTime;
    }

    public void setmUpdateTime(String mUpdateTime) {
        this.mUpdateTime = mUpdateTime;
    }
}
