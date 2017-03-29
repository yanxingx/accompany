package com.minddeveloper.accompany.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.minddeveloper.accompany.MyViews.WheelView;
import com.minddeveloper.accompany.R;
import com.minddeveloper.accompany.Utils.ServiceForXUtils;
import com.minddeveloper.accompany.Utils.StaticVariableForApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageStudentsActivity extends AppCompatActivity {
    private Context context;
    private final int FINISH_GET_STU_LIST = 1;//成功获取学生列表
    private final int ADD_STU_REQ_CODE = 2;//添加学生页请求码
    private final int TOAST_MESSAGE = 3;
    private final int FINISH_UPDATE_HEARTCOIN = 4;//成功修改心币

    private List<String[]> studentList;
    private static final String[] PLANETS = new String[]{"+3心币", "+2心币", "+1心币", "+0心币", "-1心币", "-2心币", "-3心币", "-4心币", "-5心币"};
    private int wheelViewSelectselectPosition;

    private ListView lv;

    private HandlerThread handlerThread;
    private Handler myHandler;
    private Runnable runnable1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FINISH_GET_STU_LIST:
                    lv.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return studentList.size();
                        }

                        @Override
                        public Object getItem(int position) {
                            return studentList.get(position);
                        }

                        @Override
                        public long getItemId(int position) {
                            return position;
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            ViewHolder holder = null;
                            if (convertView == null) {
                                holder = new ViewHolder();
                                convertView = LayoutInflater.from(context).inflate(R.layout.item_stu_list, null);
                                holder.nickNameTv = (TextView) convertView.findViewById(R.id.nickNameTv);
                                holder.heartCoinTv = (TextView) convertView.findViewById(R.id.heartCoinTv);
                                convertView.setTag(holder);
                            } else {
                                holder = (ViewHolder) convertView.getTag();
                            }
                            String[] data = studentList.get(position);
                            holder.nickNameTv.setText(data[2]);
                            holder.heartCoinTv.setText(data[4]);
                            return convertView;
                        }
                        final class ViewHolder {
                            public TextView nickNameTv,heartCoinTv;
                        }
                    });
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            /*final EditText coinNumEt = new EditText(context);
                            coinNumEt.setInputType(InputType.TYPE_CLASS_PHONE);
                            new AlertDialog.Builder(context).setTitle("输入正数为加心币负数为减心币")
                                    .setView(coinNumEt)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String stuid = studentList.get(position)[0];
                                            String num = coinNumEt.getText().toString();
                                            doUpdateStuHeartCoin(stuid,num);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();*/
                            wheelViewSelectselectPosition = 5;
                            View outerView = LayoutInflater.from(context).inflate(R.layout.wheel_view, null);
                            WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                            wv.setOffset(2);
                            wv.setItems(Arrays.asList(PLANETS));
                            wv.setSeletion(3);
                            wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                                @Override
                                public void onSelected(int selectedIndex, String item) {
                                    //System.out.println("[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                                    wheelViewSelectselectPosition = selectedIndex;
                                }
                            });

                            new AlertDialog.Builder(context)
                                    .setTitle("心币管理")
                                    .setView(outerView)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String stuid = studentList.get(position)[0];
                                            switch (wheelViewSelectselectPosition){
                                                case 2:
                                                    doUpdateStuHeartCoin(stuid,"3");
                                                    break;
                                                case 3:
                                                    doUpdateStuHeartCoin(stuid,"2");
                                                    break;
                                                case 4:
                                                    doUpdateStuHeartCoin(stuid,"1");
                                                    break;
                                                case 5:
                                                    break;
                                                case 6:
                                                    doUpdateStuHeartCoin(stuid,"-1");
                                                    break;
                                                case 7:
                                                    doUpdateStuHeartCoin(stuid,"-2");
                                                    break;
                                                case 8:
                                                    doUpdateStuHeartCoin(stuid,"-3");
                                                    break;
                                                case 9:
                                                    doUpdateStuHeartCoin(stuid,"-4");
                                                    break;
                                                case 10:
                                                    doUpdateStuHeartCoin(stuid,"-5");
                                                    break;
                                            }
                                        }
                                    })
                                    .show();
                        }
                    });
                    break;
                case TOAST_MESSAGE:
                    Toast.makeText(ManageStudentsActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case FINISH_UPDATE_HEARTCOIN:
                    getAllStuList();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        initData();
        initViews();
        getAllStuList();
    }
    private void initData(){
        context = this;

        handlerThread = new HandlerThread("SUBSTITUTE");
        handlerThread.start();
        myHandler = new Handler(handlerThread.getLooper());

        studentList = new ArrayList<>();
    }
    private void initViews(){
        //定义ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的学生");//紧贴返回键的标题
        toolbar.setNavigationIcon(R.drawable.bar_back);//返回键图片
        TextView toolBarRightTv = (TextView) findViewById(R.id.toolBarRightTv);
        toolBarRightTv.setVisibility(View.VISIBLE);
        toolBarRightTv.setText("添加");
        setSupportActionBar(toolbar);

        lv = (ListView) findViewById(R.id.lv);

        toolBarRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context,TeacherAddStuActivity.class),ADD_STU_REQ_CODE);
            }
        });
    }
    private void getAllStuList(){
        studentList.clear();
        HttpUtils http = ServiceForXUtils.getHaapUtils();
        http.send(HttpRequest.HttpMethod.GET, StaticVariableForApp.mainUrl+"GetAllMyStudentList?",makeParams(StaticVariableForApp.userInfoForApp.getToken()), new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if(jsonObject.getInt("code")==200){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String username = jsonObject1.getString("username");
                            String nick_name = jsonObject1.getString("nick_name");
                            String is_teacher = jsonObject1.getString("is_teacher");
                            String heart_coin = jsonObject1.getString("heart_coin");
                            studentList.add(new String[]{id,username,nick_name,is_teacher,heart_coin});
                        }
                        handler.sendEmptyMessage(FINISH_GET_STU_LIST);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void doUpdateStuHeartCoin(String stuid,String num){
        HttpUtils http = ServiceForXUtils.getHaapUtils();
        http.send(HttpRequest.HttpMethod.GET, StaticVariableForApp.mainUrl+"TeacherUpdateStuHeartCoinServlet?",makeParams2(stuid,num), new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if(jsonObject.getInt("code")==200){
                        handler.sendEmptyMessage(FINISH_UPDATE_HEARTCOIN);
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
    private RequestParams makeParams(String token) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("token", token);
        return params;
    }
    private RequestParams makeParams2(String stuid,String num) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("token", StaticVariableForApp.userInfoForApp.getToken());
        params.addQueryStringParameter("stuid", stuid);
        params.addQueryStringParameter("num", num);
        return params;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ADD_STU_REQ_CODE:
                if(resultCode==RESULT_OK){
                    getAllStuList();
                }
                break;
        }
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
}
