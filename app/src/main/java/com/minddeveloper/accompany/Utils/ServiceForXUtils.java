package com.minddeveloper.accompany.Utils;

import com.lidroid.xutils.HttpUtils;

/**
 * @Title         ServiceForXUtils.java
 * @Description  xUtils SDK服务类
 * @author        Created by 闫兴 on 2016/11/10.
 * @e-mail        yanxingx@163.com
 */
public class ServiceForXUtils {
    private static HttpUtils httpUtils;
    public static HttpUtils getHaapUtils(){
        if(httpUtils == null){
            httpUtils = new HttpUtils();
            httpUtils.configTimeout(20000);
            httpUtils.configRequestThreadPoolSize(10);
            httpUtils.configCurrentHttpCacheExpiry(0);
            httpUtils.configResponseTextCharset("UTF-8");
        }
        return httpUtils;
    }
}
