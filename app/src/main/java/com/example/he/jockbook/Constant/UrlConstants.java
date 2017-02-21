package com.example.he.jockbook.Constant;

import java.util.Random;

/**
 * Jock、Image数据的URL
 * Created by he on 2017/2/21.
 */

public class UrlConstants {

    private static Random random=new Random(47);
    public static final String NEWS_JOCK_URL="http://japi.juhe.cn/joke/content/text.from?key=aea3530fe10ee7947528d76711ee7796&page=1&pagesize=10";
    public static final String RANDOM_JOCK_URL="http://japi.juhe.cn/joke/content/text.from?key=aea3530fe10ee7947528d76711ee7796&page="+""+random.nextInt(40000)+"&pagesize=10";
    public static final String NEWS_IMAGE_URL="http://japi.juhe.cn/joke/img/text.from?key=aea3530fe10ee7947528d76711ee7796&page=1&pagesize=10";
    public static final String RANDOM_IMAGE_URL="http://v.juhe.cn/joke/randJoke.php?key=aea3530fe10ee7947528d76711ee7796&type=pic";

}
