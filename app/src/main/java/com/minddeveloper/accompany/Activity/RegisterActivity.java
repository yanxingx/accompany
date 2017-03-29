package com.minddeveloper.accompany.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.minddeveloper.accompany.R;
import com.minddeveloper.accompany.Service.LoginService;
import com.minddeveloper.accompany.Utils.ServiceForXUtils;
import com.minddeveloper.accompany.Utils.StaticVariableForApp;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private Context context;
    private final int TOAST_MESSAGE = 1;
    private final int LOGIN_SUCCESS = 2;//注册后登录成功
    private final int LOGIN_FAIL = 3;//注册后登录失败
    private LoginService.MyBinder myBinder;
    private Intent loginServiceIntent;
    private ServiceConnection loginServiceConn;

    private EditText accountEt,passwdEt,passwdEt2;

    private HandlerThread handlerThread;
    private Handler myHandler;
    private Runnable runnable1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TOAST_MESSAGE:
                    Toast.makeText(RegisterActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_SUCCESS:
                    setResult(RESULT_OK);
                    finish();
                    break;
                case LOGIN_FAIL:
                    Toast.makeText(RegisterActivity.this, "注册后登录失败,请手动登录", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initData();
        bindLoginService();
        initViews();
    }
    private void initData(){
        context = this;

        handlerThread = new HandlerThread("SUBSTITUTE");
        handlerThread.start();
        myHandler = new Handler(handlerThread.getLooper());
    }
    private void bindLoginService(){
        loginServiceIntent = new Intent(context,LoginService.class);
        loginServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBinder = (LoginService.MyBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(loginServiceIntent,loginServiceConn,BIND_AUTO_CREATE);
        startService(loginServiceIntent);
    }
    private void initViews(){
        //定义ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("注册");//紧贴返回键的标题
        toolbar.setNavigationIcon(R.drawable.bar_back);//返回键图片
        setSupportActionBar(toolbar);

        accountEt = (EditText) findViewById(R.id.accountEt);
        passwdEt = (EditText) findViewById(R.id.passwdEt);
        passwdEt2 = (EditText) findViewById(R.id.passwdEt2);

        findViewById(R.id.regBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEt.getText().toString();
                String passwd1 = passwdEt.getText().toString();
                String passwd2 = passwdEt2.getText().toString();
                if(!account.equals("")){
                    if(!passwd1.equals("")){
                        if(!passwd2.equals("")){
                            if(passwd1.equals(passwd2)){
                                doLogin(account,passwd1);
                            }else {
                                Toast.makeText(RegisterActivity.this, "再次输入的密码不一致", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(RegisterActivity.this, "请再次输入密码", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void doLogin(final String username, final String password){
        HttpUtils http = ServiceForXUtils.getHaapUtils();
        http.send(HttpRequest.HttpMethod.POST, StaticVariableForApp.mainUrl+"RegisterServlet?", makeParams(username,password),
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        try {
                            JSONObject jsonObject = new JSONObject(arg0.result);
                            //System.out.println(jsonObject);
                            if(jsonObject.getInt("code")==200){
                                myBinder.loginByPasswd(context,username,password,handler,LOGIN_SUCCESS,LOGIN_FAIL);
                            }
                            Message m = new Message();
                            m.what = TOAST_MESSAGE;
                            m.obj = jsonObject.getString("message");
                            handler.sendMessage(m);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private RequestParams makeParams(String username,String password) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);
        return params;
    }
    @Override
    protected void onDestroy() {
        unbindService(loginServiceConn);
        stopService(loginServiceIntent);
        super.onDestroy();
    }
}
