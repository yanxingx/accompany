package com.minddeveloper.accompany.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.minddeveloper.accompany.Bean.UserInfoForApp;
import com.minddeveloper.accompany.Utils.ServiceForXUtils;
import com.minddeveloper.accompany.Utils.SharedPreferencesService;
import com.minddeveloper.accompany.Utils.StaticVariableForApp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Title         LoginService.java
 * @Description  登录Service
 * @author        Created by 闫兴 on 2016/11/17.
 * @e-mail        yanxingx@163.com
 */
public class LoginService extends Service {
    private MyBinder myBinder;
    private SharedPreferencesService sharedPreferencesService;

    public LoginService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("登录服务创建");
        initData();
    }
    private void initData(){
        myBinder = new MyBinder();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("登录服务开启");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        System.out.println("登录服务销毁");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("登录服务绑定");
        return myBinder;
    }
    public class MyBinder extends Binder{//自定义Binder
        public void getLoginState(Context context,Handler handler,final int LOGIN_SUCCESS_CODE, final int LOGIN_FAIL_CODE,int HAVENT_LOGIN_CODE){//获取用户登录状态
            //检查本地是否存有token
            SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(context);
            String token = sharedPreferencesService.getToken();
            if(!token.equals("")){//用户登录过并且本地已存token值
                //开始通过token登录
                loginByToken(token,handler,LOGIN_SUCCESS_CODE, LOGIN_FAIL_CODE);
            }else {//用户未曾登录
                handler.sendEmptyMessage(HAVENT_LOGIN_CODE);
            }
        }
        public void loginByPasswd(final Context context, String account, String passwd, final Handler handler, final int SUCCESS_CODE, final int FAIL_CODE){//通过账号密码登录
            HttpUtils http = ServiceForXUtils.getHaapUtils();
            http.send(HttpRequest.HttpMethod.POST, StaticVariableForApp.mainUrl+"LoginServlet?", makeParams(account,passwd),
                    new RequestCallBack<String>() {

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {

                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> arg0) {
                            try {
                                JSONObject jsonObject = new JSONObject(arg0.result);
                                System.out.println("来自服务"+jsonObject);
                                if(jsonObject.getInt("code")==200){
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    String id = jsonObject1.getString("id");
                                    String username = jsonObject1.getString("username");
                                    String nick_name = jsonObject1.getString("nick_name");
                                    String heart_coin = jsonObject1.getString("heart_coin");
                                    String is_teacher = jsonObject1.getString("is_teacher");
                                    String token = jsonObject1.getString("token");
                                    StaticVariableForApp.userInfoForApp = new UserInfoForApp(id,username,nick_name,heart_coin,is_teacher,token);//将登录后用户信息存储到内存中
                                    StaticVariableForApp.userInfoForApp.setLogin(true);
                                    sharedPreferencesService = new SharedPreferencesService(context);
                                    sharedPreferencesService.saveToken(token);//存储token到本地
                                    Message m = new Message();
                                    m.what = SUCCESS_CODE;
                                    m.obj = jsonObject.getString("message");
                                    handler.sendMessage(m);
                                }else {
                                    Message m = new Message();
                                    m.what = FAIL_CODE;
                                    m.obj = jsonObject.getString("message");
                                    handler.sendMessage(m);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        public void loginByToken(final String token, final Handler handler, final int SUCCESS_CODE, final int FAIL_CODE){//通过token登录
            HttpUtils http = ServiceForXUtils.getHaapUtils();
            http.send(HttpRequest.HttpMethod.POST, StaticVariableForApp.mainUrl+"LoginByTokenServlet", makeParams2(token),
                    new RequestCallBack<String>() {

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {

                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> arg0) {
                            try {
                                JSONObject jsonObject = new JSONObject(arg0.result);
                                System.out.println("来自服务"+jsonObject);
                                if(jsonObject.getInt("code")==200){
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    String id = jsonObject1.getString("id");
                                    String username = jsonObject1.getString("username");
                                    String nick_name = jsonObject1.getString("nick_name");
                                    String heart_coin = jsonObject1.getString("heart_coin");
                                    String is_teacher = jsonObject1.getString("is_teacher");
                                    StaticVariableForApp.userInfoForApp = new UserInfoForApp(id,username,nick_name,heart_coin,is_teacher,token);//将登录后用户信息存储到内存中
                                    StaticVariableForApp.userInfoForApp.setLogin(true);
                                    handler.sendEmptyMessage(SUCCESS_CODE);
                                }else {
                                    handler.sendEmptyMessage(FAIL_CODE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }
    private RequestParams makeParams(String account, String passwd) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("username", account);
        params.addBodyParameter("password", passwd);
        return params;
    }
    private RequestParams makeParams2(String token) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token",token);
        return params;
    }
}
