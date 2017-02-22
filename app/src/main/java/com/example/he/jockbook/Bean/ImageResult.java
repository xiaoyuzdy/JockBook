package com.example.he.jockbook.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by he on 2017/2/22.
 */

public class ImageResult {
    @SerializedName("data")
    public List<ImageData> imageDatas;
}
