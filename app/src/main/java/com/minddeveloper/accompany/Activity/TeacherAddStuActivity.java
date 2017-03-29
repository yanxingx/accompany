package com.minddeveloper.accompany.Activity;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.minddeveloper.accompany.R;
import com.minddeveloper.accompany.Utils.ServiceForXUtils;
import com.minddeveloper.accompany.Utils.StaticVariableForApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeacherAddStuActivity extends AppCompatActivity {
    private Context context;
    private final int FINISH_GET_STU_LIST = 1;//成功获取学生列表
    private final int ADD_STU_SUCCESS = 2;//成功添加学生
    private final int TOAST_MESSAGE = 3;

    private List<String[]> studentList;

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
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String stuid = studentList.get(position)[0];
                            doClaimStu(stuid);
                        }
                    });
                    break;
                case ADD_STU_SUCCESS:
                    TeacherAddStuActivity.this.setResult(RESULT_OK);
                    finish();
                    break;
                case TOAST_MESSAGE:
                    Toast.makeText(TeacherAddStuActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_stu);

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
        toolbar.setTitle("添加学生");//紧贴返回键的标题
        toolbar.setNavigationIcon(R.drawable.bar_back);//返回键图片
        setSupportActionBar(toolbar);

        lv = (ListView) findViewById(R.id.lv);
    }
    private void getAllStuList(){
        HttpUtils http = ServiceForXUtils.getHaapUtils();
        http.send(HttpRequest.HttpMethod.GET, StaticVariableForApp.mainUrl+"GetAllStudentList?",makeParams(StaticVariableForApp.userInfoForApp.getToken()), new RequestCallBack<String>() {

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
    private void doClaimStu(String stuid){
        HttpUtils http = ServiceForXUtils.getHaapUtils();
        http.send(HttpRequest.HttpMethod.GET, StaticVariableForApp.mainUrl+"TeacherClaimStudentServlet",makeParams2(stuid), new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if(jsonObject.getInt("code")==200){
                        handler.sendEmptyMessage(ADD_STU_SUCCESS);
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
    private RequestParams makeParams2(String stuid) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("token",StaticVariableForApp.userInfoForApp.getToken());
        params.addQueryStringParameter("stuid", stuid);
        return params;
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
