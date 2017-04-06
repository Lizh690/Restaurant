package com.lee.restaurant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lee.restaurant.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 注册界面
 * <p>
 * Created by ZHB on 2016/10/1.
 */

public class RegistActivity extends BaseActivity implements View.OnClickListener {

    // 手机号输入框
    private EditText inputPhoneEt;

    // 验证码输入框
    private EditText inputCodeEt;

    // 获取验证码按钮
    private Button requestCodeBtn;

    // 注册按钮
    private Button commitBtn;

    // 昵称
    private EditText inputNameEt;
    //密码
    private EditText inputpassEt;
    //确认密码
    private EditText inputpassagainEt;
    //地址
    private EditText inputaddressEt;

    int i = 60;
    private ProgressBar mProBar;

    private StringBuffer sb;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_activity);

        init();

        getSupportActionBar().setTitle("注册");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * 初始化控件
     */
    private void init() {

        inputNameEt = (EditText) findViewById(R.id.addname_tv);
        inputpassEt = (EditText) findViewById(R.id.addpass_tv);
        inputpassagainEt = (EditText) findViewById(R.id.addpassagain_tv);
        inputaddressEt = (EditText) findViewById(R.id.address_et);


        inputPhoneEt = (EditText) findViewById(R.id.login_input_phone_et);
        inputCodeEt = (EditText) findViewById(R.id.login_input_code_et);
        requestCodeBtn = (Button) findViewById(R.id.login_request_code_btn);
        commitBtn = (Button) findViewById(R.id.login_commit_btn);

        sb = new StringBuffer();

        requestCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);

        // 启动短信验证sdk
        SMSSDK.initSDK(this, "179cfd54c3eea", "64461eb3f1fe71a00c142895030e8ca3");
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onClick(View v) {
        String phoneNums = inputPhoneEt.getText().toString();
        switch (v.getId()) {
            case R.id.login_request_code_btn:
                // 1. 通过规则判断手机号
                if (!judgePhoneNums(phoneNums)) {
                    return;
                } // 2. 通过sdk发送短信验证
                SMSSDK.getVerificationCode("86", phoneNums);

                // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                requestCodeBtn.setClickable(false);
                requestCodeBtn.setText("重新发送(" + i + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            handler.sendEmptyMessage(-9);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);
                    }
                }).start();
                break;

            case R.id.login_commit_btn:
                SMSSDK.submitVerificationCode("86", phoneNums, inputCodeEt
                        .getText().toString());
                createProgressBar();

                break;
        }
    }

    /**
     *
     */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                requestCodeBtn.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                requestCodeBtn.setText("获取验证码");
                requestCodeBtn.setClickable(true);
                i = 30;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功

                        Toast.makeText(getApplicationContext(), "提交验证码成功",
                                Toast.LENGTH_SHORT).show();


                        new Thread(CommitSelver).start();


                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();

                    }
                }
            }
        }
    };

    private Thread CommitSelver = new Thread() {
        @Override
        public void run() {

            String name = inputNameEt.getText().toString();
            String pass = inputpassEt.getText().toString();
            String passscend = inputpassagainEt.getText().toString();
            String address = inputaddressEt.getText().toString().trim();
            String phone = inputPhoneEt.getText().toString();

            String url;


            if (!pass.equals(passscend)) {
                //密码不一致
                Toast.makeText(getApplicationContext(), "两次输入密码不一致", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                url = "http://123.206.221.174/app/zhuce.php?name=" + URLEncoder.encode(name, "utf-8") + "&pass=" + URLEncoder.encode(pass, "utf-8") +
                        "&address=" + URLEncoder.encode(address, "utf-8") +
                        "&phone=" + URLEncoder.encode(phone, "utf-8");

                //url = "https://api.github.com/users/lqy1994/repos";
                Log.i("------>url", url);

                URL httpurl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String str;
                while ((str = read.readLine()) != null) {
                    sb.append(str);
                }
                Log.i("------>sb", sb.toString());

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ("success".equals(Analysis(sb.toString()))) {
                        SharedPreferences preferences = getSharedPreferences("user", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("name", inputNameEt.getText().toString());
                        editor.putString("pass", inputpassEt.getText().toString());
                        editor.putString("address", inputaddressEt.getText().toString().trim());
                        editor.putString("phone", inputPhoneEt.getText().toString());
                        editor.commit();

                        Log.i("------>", "注册界面存储成功");

                        mProBar.setVisibility(View.GONE);
                        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();


                    } else {
                        mProBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), Analysis(sb.toString()).toString(), Toast.LENGTH_LONG).show();
                        Log.i("------>", "注册失败，字符串不相等");
                    }
                }
            });


        }

    };

    public String Analysis(String jsonStr) {
        String states;
        String message = null;

        //{"states":"1","message":"success"}
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            states = jsonObj.getString("states");
            message = jsonObj.getString("message");

            Log.i("---states---", states);
            Log.i("---message---", message + "\t" + message.length());

        } catch (JSONException e) {
            Log.i("---------", "json解析失败");
            e.printStackTrace();
        }
        return message;
    }


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188、178
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][3587]\\d{9}";// "[1]"代表第1位为数字1，"[3587]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /**
     * progressbar
     */
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

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Regist Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
