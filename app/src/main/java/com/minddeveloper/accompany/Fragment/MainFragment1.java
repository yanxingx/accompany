package com.minddeveloper.accompany.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
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

public class MainFragment1 extends Fragment {
    private Context context;
    private final int FINISH_GET_STU_LIST = 1;
    private final int TOAST_MESSAGE = 2;
    private List<String[]> studentList;

    private ListView lv;
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
                    break;
                case TOAST_MESSAGE:
                    Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initData();
        View view = initViews(inflater,container);

        return view;
    }
    private void initData(){
        context = this.getActivity();
        studentList = new ArrayList<>();
    }
    private View initViews(LayoutInflater inflater,ViewGroup container){
        View view = inflater.inflate(R.layout.fragment_main_fragment1, container, false);
        lv = (ListView) view.findViewById(R.id.lv);
        getAllStuList();
        return view;
    }
    private void getAllStuList(){
        HttpUtils http = ServiceForXUtils.getHaapUtils();
        http.send(HttpRequest.HttpMethod.GET, StaticVariableForApp.mainUrl+"GetAllStudentWithoutToken?",null, new RequestCallBack<String>() {

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
}
