package com.minddeveloper.accompany.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Title         SharedPreferencesService.java
 * @Description  SharedPreferences服务类
 * @author        Created by 闫兴 on 2016/11/18.
 * @e-mail        yanxingx@163.com
 */
public class SharedPreferencesService {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public SharedPreferencesService(Context context){
        this.context = context;
        preferences = context.getSharedPreferences("DATA_FOR_APP", Context.MODE_PRIVATE);
    }
    public void saveToken(String token){
        editor = preferences.edit();
        editor.putString("TOKEN", token);
        editor.commit();
    }
    public String getToken(){
        String token = preferences.getString("TOKEN","");
        return token;
    }
}
