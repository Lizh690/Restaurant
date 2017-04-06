package com.lee.restaurant.Activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.restaurant.Fragment.PastFragment;
import com.lee.restaurant.Fragment.PastNoneFragment;
import com.lee.restaurant.Model.PastData;
import com.lee.restaurant.Model.PastDataList;
import com.lee.restaurant.R;
import com.lee.restaurant.Service.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.lee.restaurant.R.id.toolbar;


/**
 * 历史账单
 * Created by ZHB on 2016/10/2.
 */

public class PastActivity extends BaseActivity {

    public FragmentTransaction transaction;
    public FragmentManager fragment;
    public PastFragment pastFragment;
    public PastNoneFragment pastNoneFragment;
    public ProgressBar mProBar;

    public static List<PastDataList> pastdatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_activity);

        getSupportActionBar().setTitle("历史菜单");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PastActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        new Thread(GetPastThread).start();
        createProgressBar();

    }

    private Thread GetPastThread = new Thread() {
        @Override
        public void run() {
            SharedPreferences preferences = getSharedPreferences("user", Activity.MODE_PRIVATE);
            String uid = preferences.getString("phone", "").toString().trim();
            String url = "http://123.206.221.174/app/history_order.php?uid=" + uid;
            try {
                URL httpurl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sBuffer = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {
                    sBuffer.append(str);
                }

                Boolean bool = JsonPastData(sBuffer.toString());

                if (bool) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mProBar.setVisibility(View.GONE);
                            fragment = getFragmentManager();
                            transaction = fragment.beginTransaction();
                            pastFragment = new PastFragment();
                            transaction.replace(R.id.past_fragment, pastFragment);
                            transaction.commit();
                        }
                    });
                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProBar.setVisibility(View.GONE);
                            fragment = getFragmentManager();
                            transaction = fragment.beginTransaction();
                            pastNoneFragment = new PastNoneFragment();
                            transaction.replace(R.id.past_fragment, pastNoneFragment);
                            transaction.commit();
                        }
                    });
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    };

    private Boolean JsonPastData(String jsonstring) {
        try {
            JSONObject jsonobject = new JSONObject(jsonstring);

            int status = jsonobject.getInt("status");


            if (status == 1) {
                pastdatas = new ArrayList<>();
                JSONArray jsonarraydata = jsonobject.getJSONArray("data");

                for (int i = 0; i < jsonarraydata.length(); i++) {
                    PastDataList data = new PastDataList();
                    JSONObject objectdata = (JSONObject) jsonarraydata.opt(i);
                    data.setOtime(objectdata.getString("otime"));
                    data.setOsum(objectdata.getString("osum"));
                    data.setTips(objectdata.getString("tips"));
                    data.setOpay(objectdata.getString("opay"));
                    data.setOstate(objectdata.getString("ostate"));
                    data.setOid(objectdata.getString("oid"));

                    JSONArray jsonArraydish = objectdata.getJSONArray("dish");
                    List<PastDataList.DishesBeanlist> dishlists = new ArrayList<>();

                    for (int j = 0; j < jsonArraydish.length(); j++) {
                        PastDataList.DishesBeanlist dishesBean = new PastDataList.DishesBeanlist();
                        JSONObject jsonObjectdish = (JSONObject) jsonArraydish.opt(j);
                        dishesBean.setName(jsonObjectdish.getString("name"));
                        dishesBean.setNum(jsonObjectdish.getString("num"));
                        dishesBean.setPicture(jsonObjectdish.getString("picture"));

                        dishlists.add(dishesBean);
                    }
                    data.setDishes(dishlists);

                    pastdatas.add(data);
                }
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createProgressBar() {

        FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mProBar = new ProgressBar(this);
        mProBar.setLayoutParams(layoutParams);
        mProBar.setVisibility(View.VISIBLE);
        layout.addView(mProBar);

    }

}
