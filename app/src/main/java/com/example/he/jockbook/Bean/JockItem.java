package com.example.he.jockbook.Bean;

/**
 * RecyclerView的item对应的实体类
 * Created by he on 2017/2/19.
 */

public class JockItem {
    private String updateTime;
    private String content;

    public JockItem(String content, String updateTime) {
        this.content = content;
        this.updateTime = updateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
