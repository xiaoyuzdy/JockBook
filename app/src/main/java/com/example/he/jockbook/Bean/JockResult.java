package com.example.he.jockbook.Bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * GSON解析笑话JSON数据对应的实体类
 * Created by he on 2017/2/18.
 */

public class JockResult {
    @SerializedName("data")
    public List<JockData> jockDatas;
}
