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

import com.minddeveloper.accompany.R;
import com.minddeveloper.accompany.Service.LoginService;

public class LoginActivity extends AppCompatActivity {
    private Context context;
    private LoginService.MyBinder myBinder;
    private final int LOGIN_SUCCESS = 1;
    private final int LOGIN_FAIL = 2;
    private final int REGIS_REQ_CODE = 3;//跳转到登录页请求码
    private Intent loginServiceIntent;
    private ServiceConnection loginServiceConn;

    private EditText accountEt,passwdEt;

    private HandlerThread handlerThread;
    private Handler myHandler;
    private Runnable runnable1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOGIN_SUCCESS:
                    LoginActivity.this.setResult(RESULT_OK);
                    finish();
                    break;
                case LOGIN_FAIL:
                    Toast.makeText(LoginActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        toolbar.setTitle("登录");//紧贴返回键的标题
        toolbar.setNavigationIcon(R.drawable.bar_back);//返回键图片
        setSupportActionBar(toolbar);

        accountEt = (EditText) findViewById(R.id.accountEt);
        passwdEt = (EditText) findViewById(R.id.passwdEt);

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEt.getText().toString();
                String passwd = passwdEt.getText().toString();
                if(!account.equals("")){
                    if(!passwd.equals("")){
                        myBinder.loginByPasswd(context,account,passwd,handler,LOGIN_SUCCESS,LOGIN_FAIL);
                    }else {
                        Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.regTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context,RegisterActivity.class),REGIS_REQ_CODE);
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

    @Override
    protected void onDestroy() {
        unbindService(loginServiceConn);
        stopService(loginServiceIntent);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REGIS_REQ_CODE&&resultCode == RESULT_OK){
            LoginActivity.this.setResult(RESULT_OK);
            finish();
        }
    }
}
