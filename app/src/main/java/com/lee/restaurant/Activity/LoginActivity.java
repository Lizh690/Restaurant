package com.lee.restaurant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.lee.restaurant.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LoginActivity extends BaseActivity {

    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private TextView registTextView;
    private TextView lostpass;

    ProgressBar mProBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 电话号码
        mPhoneView = (AutoCompleteTextView) findViewById(R.id.phone);
        mPasswordView = (EditText) findViewById(R.id.password);


        //toolbar.setTitle("登录");
        getSupportActionBar().setTitle("登录");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        SharedPreferences preferences = getSharedPreferences("user",Activity.MODE_PRIVATE);
        if(preferences.getString("phone","") != null)
        {
            mPhoneView.setText(preferences.getString("phone",""));
            mPasswordView.setText(preferences.getString("pass",""));
        }


        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        registTextView = (TextView)findViewById(R.id.regist_TextView);
        //  跳到注册
        registTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        // 忘记密码
        lostpass = (TextView)findViewById(R.id.lostpass);
        lostpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,LostPassSMSActivity.class);
                intent.putExtra("activity","lost");
                startActivity(intent);
            }
        });

    }

    // 登录
    public void LoginOK(View view) {

        //Toast.makeText(getApplicationContext(),"开始访问",Toast.LENGTH_LONG).show();
        new Thread(GetThread).start();
        createProgressBar();

    }


    private Thread GetThread = new Thread(){

        @Override
        public void run() {
            String personphone = mPhoneView.getText().toString();
            String personpass = mPasswordView.getText().toString();

            StringBuffer sBuffer = null;
            String url = null;
            try {
                url =  "http://123.206.221.174/app/login.php?personphone="+personphone+"&personpass="+personpass;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Log.i("-------url",url);
                URL httpurl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str;
                sBuffer = new StringBuffer();
                while((str = reader.readLine()) != null )
                {
                    sBuffer.append(str);
                }
                reader.close();
                if(Analysis(sBuffer.toString()))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mProBar.setVisibility(View.GONE);
                            Toast.makeText(getApplication(),"登陆成功",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProBar.setVisibility(View.GONE);
                            Toast.makeText(getApplication(),"登陆失败",Toast.LENGTH_LONG).show();
                            mPhoneView.setText("");
                            mPasswordView.setText("");
                        }
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
       }
    };


    public Boolean Analysis(String jsonStr){

        String states = null;
        String message = null ;
        String name = null;
        String address = null ;

        //{"states":"1","message":"success"}
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            states = jsonObj.getString("states");
            message = jsonObj.getString("message");
            name = jsonObj.getString("name");
            address = jsonObj.getString("address");


            Log.i("--states---",states);
            //Log.i("--message---",message + "\t" + message.length());

        } catch (JSONException e) {
            System.out.println("Json parse error");
            e.printStackTrace();
        }

        if(states.equals("1"))
        {
            SharedPreferences preferences = getSharedPreferences("user",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("phone",mPhoneView.getText().toString());
            editor.putString("pass",mPasswordView.getText().toString());
            editor.putString("name",name.toString());
            editor.putString("address",address.toString());
            editor.commit();

            //Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_LONG).show();
            return  true;
        }

        return false;
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

}
