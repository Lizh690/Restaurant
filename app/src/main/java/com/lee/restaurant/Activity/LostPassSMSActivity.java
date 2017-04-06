package com.lee.restaurant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.lee.restaurant.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by ZHB on 2016/10/3.
 */

public class LostPassSMSActivity extends BaseActivity implements View.OnClickListener{
    // 手机号输入框
    private EditText inputPhoneEt;

    // 验证码输入框
    private EditText inputCodeEt;

    // 获取验证码按钮
    private Button requestCodeBtn;

    // 注册按钮
    private Button commitBtn;

    private ProgressBar mProBar;

    //
    int i = 60;


    public  static int RESULT_CODE = 1;
    private String infon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostpasssms_activity);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        infon  = bundle.getString("activity");

        getSupportActionBar().setTitle("验证密码");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                //Toast.makeText(getApplicationContext(),"返回",Toast.LENGTH_SHORT).show();

                if(infon.toString().equals("change"))
                {
                     intent = new Intent(LostPassSMSActivity.this,ChangeActivity.class);

                }else {
                    intent = new Intent(LostPassSMSActivity.this,LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });

        init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        inputPhoneEt = (EditText) findViewById(R.id.lost_input_phone_et);
        inputCodeEt = (EditText) findViewById(R.id.lost_input_code_et);
        requestCodeBtn = (Button) findViewById(R.id.lost_request_code_btn);
        commitBtn = (Button) findViewById(R.id.lost_commit_btn);
        requestCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);

        // 启动短信验证sdk
        SMSSDK.initSDK(this, "179cfd54c3eea", "64461eb3f1fe71a00c142895030e8ca3");
        EventHandler eventHandler = new EventHandler(){
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
            case R.id.lost_request_code_btn:
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

            case R.id.lost_commit_btn:
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

                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        Toast.makeText(getApplicationContext(), "提交验证码成功",
                                Toast.LENGTH_SHORT).show();

                        if(infon.equals("change"))
                        {
                            Intent intent = new Intent();
                            intent.putExtra("back","sucess");
                            intent.putExtra("phone",inputPhoneEt.getText().toString());
                            setResult(RESULT_CODE,intent);
                            finish();

                        }else {
                            SharedPreferences preferences = getSharedPreferences("resetpass",Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("phone",inputPhoneEt.getText().toString());
                            editor.commit();

                            //mProBar.setVisibility(View.GONE);

                            Log.i("验证成功",inputPhoneEt.getText().toString());
                            Intent intent = new Intent(LostPassSMSActivity.this,
                                    ResetPassActivity.class);
                            startActivity(intent);
                            finish();
                        }


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
        Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
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
        String telRegex = "[1][3578]\\d{9}";// "[1]"代表第1位为数字1，"[3587]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
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
}
