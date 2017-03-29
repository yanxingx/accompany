package com.minddeveloper.accompany.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minddeveloper.accompany.Bean.UserInfoForApp;
import com.minddeveloper.accompany.Fragment.MainFragment1;
import com.minddeveloper.accompany.R;
import com.minddeveloper.accompany.Service.LoginService;
import com.minddeveloper.accompany.Utils.StaticVariableForApp;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private final int LOGIN_SUCCESS_CODE = 1;//用户已经通过token登录
    private final int LOGIN_FAIL_CODE = 2;//用户通过token登录失败(token失效)
    private final int HAVENT_LOGIN_CODE = 3;//用户从未登录过
    private final int START_CHECK_LOGIN = 4;//开始判断登录状态
    private final int LOGIN_REQ_CODE = 5;//跳转到登录页请求码

    private Intent loginServiceIntent;
    private ServiceConnection loginServiceConn;
    private LoginService.MyBinder myBinder;

    private TextView toolbarTitleTv,userNameTv;
    private DrawerLayout drawer_layout;
    private LinearLayout drawer,myStudentsLin;
    private ViewPager vp;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOGIN_SUCCESS_CODE:
                    UserInfoForApp userInfoForApp = StaticVariableForApp.userInfoForApp;
                    String userNickName = userInfoForApp.getNick_name();
                    if(userNickName.equals("")) {
                        userNameTv.setText(userInfoForApp.getUsername());
                    }else {
                        userNameTv.setText(userNickName);
                    }
                    if(userInfoForApp.getIs_teacher().equals("1")){
                        myStudentsLin.setVisibility(View.VISIBLE);
                        myStudentsLin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(context,ManageStudentsActivity.class));
                            }
                        });
                    }else {
                        myStudentsLin.setVisibility(View.GONE);
                    }
                    break;
                case LOGIN_FAIL_CODE:
                    break;
                case HAVENT_LOGIN_CODE:
                    break;
                case START_CHECK_LOGIN:
                    myBinder.getLoginState(context,handler,LOGIN_SUCCESS_CODE,LOGIN_FAIL_CODE,HAVENT_LOGIN_CODE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        bindLoginService();
        initViews();
    }
    private void initData(){
        context = this;
    }
    private void bindLoginService(){
        loginServiceIntent = new Intent(context,LoginService.class);
        loginServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBinder = (LoginService.MyBinder) service;
                handler.sendEmptyMessage(START_CHECK_LOGIN);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(loginServiceIntent,loginServiceConn,BIND_AUTO_CREATE);
        startService(loginServiceIntent);
    }
    private void initViews() {
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer = (LinearLayout) findViewById(R.id.drawer);
        myStudentsLin = (LinearLayout) findViewById(R.id.myStudentsLin);

        userNameTv = (TextView) findViewById(R.id.userNameTv);
        vp = (ViewPager) findViewById(R.id.vp);
        final List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MainFragment1());
        fragmentList.add(new Fragment());
        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int zeroNum = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0){
                    if(positionOffsetPixels == 0) {
                        if (zeroNum == 23) {//当前页position为零且滑动偏移值positionOffsetPixels持续出现指定个零
                            drawer_layout.openDrawer(drawer);
                            zeroNum = 0;
                            return;
                        }
                        zeroNum++;
                    }else {
                        zeroNum = 0;
                    }
                }
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        findViewById(R.id.headImgArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LoginActivity.class);
                startActivityForResult(intent,LOGIN_REQ_CODE);
            }
        });
        findViewById(R.id.personPagBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(drawer);
            }
        });
        /*//定义ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");//紧贴返回键的标题
        //toolbar.setNavigationIcon(R.drawable.bar_back);//返回键图片
        setSupportActionBar(toolbar);

        toolbarTitleTv = (TextView) findViewById(R.id.toolbarTitleTv);
        toolbarTitleTv.setText("主页");//顶部居中的标题*/
    }
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    @Override
    protected void onDestroy() {
        unbindService(loginServiceConn);
        stopService(loginServiceIntent);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case LOGIN_REQ_CODE://登录页返回结果
                if(resultCode == RESULT_OK){
                    handler.sendEmptyMessage(LOGIN_SUCCESS_CODE);
                }
                break;
        }
    }
}
