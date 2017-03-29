package com.minddeveloper.accompany.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.minddeveloper.accompany.R;

public class WelcomeActivity extends AppCompatActivity {
    private Context context;
    private final int GOTO_MAIN_PAGE = 1;//跳转到主页

    private HandlerThread handlerThread;
    private Handler myHandler;
    private Runnable runnable1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GOTO_MAIN_PAGE:
                    startActivity(new Intent(context,MainActivity.class));
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initData();

        runnable1 = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(GOTO_MAIN_PAGE);
            }
        };
        myHandler.postDelayed(runnable1,2000);
    }

    private void initData() {
        context = this;

        handlerThread = new HandlerThread("SUBSTITUTE");
        handlerThread.start();
        myHandler = new Handler(handlerThread.getLooper());
    }
}
