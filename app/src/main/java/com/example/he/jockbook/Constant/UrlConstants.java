package com.example.he.jockbook.Constant;

import java.util.Random;

/**
 * Jock、Image数据的URL
 * Created by he on 2017/2/21.
 */

public class UrlConstants {

    private static Random random = new Random();
    public static final String NEWS_JOCK_URL = "http://japi.juhe.cn/joke/content/text.from?key=aea3530fe10ee7947528d76711ee7796&page=1&pagesize=20";
    public static final String NEWS_IMAGE_URL = "http://japi.juhe.cn/joke/img/text.from?key=aea3530fe10ee7947528d76711ee7796&page=1&pagesize=20";


    public static String getJockRandomUrl() {
        int jockRand = random.nextInt(20000);
        return "http://japi.juhe.cn/joke/content/text.from?key=aea3530fe10ee7947528d76711ee7796&page=" + jockRand + "&pagesize=20";

    }

    public static String getImageRandomUrl() {
        int imageRand = random.nextInt(5000);
        return "http://japi.juhe.cn/joke/img/text.from?key=aea3530fe10ee7947528d76711ee7796&page=" + imageRand + "&pagesize=20";
    }

}
