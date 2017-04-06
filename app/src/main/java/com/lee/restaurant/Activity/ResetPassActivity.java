package com.lee.restaurant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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

/**
 *  重新设置密码
 *
 * Created by ZHB on 2016/10/3.
 */

public class ResetPassActivity extends BaseActivity implements View.OnClickListener{
    private EditText resetpass;
    private EditText resetpassagain;
    private Button resetpassbutton;

    private ProgressBar mProBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpass_activity);
        resetpass = (EditText)findViewById(R.id.resetpass_et);
        resetpassagain = (EditText)findViewById(R.id.resetpassagain_et);
        resetpassbutton = (Button) findViewById(R.id.resetpass_bt);
        resetpassbutton.setOnClickListener(this);


        getSupportActionBar().setTitle("更改密码");
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPassActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {

        String pass = resetpass.getText().toString();
        String passagain = resetpassagain.getText().toString();

        if(pass.equals(passagain)){
            new Thread(CommitPass).start();
            createProgressBar();

        }else {

            Toast.makeText(getApplicationContext(),"两次输入密码不一致",Toast.LENGTH_LONG).show();
        }

    }


    private  Thread CommitPass = new Thread(){

        @Override
        public void run() {

            String pass = resetpass.getText().toString();

            SharedPreferences preferences = getSharedPreferences("resetpass",Activity.MODE_PRIVATE);
            String phone = preferences.getString("phone","");

            if(!phone.equals("")){

                StringBuffer sBuffer = null;
                String url = null;
                try {
                    url = "http://123.206.221.174/app/forgetpw.php?phone="+ URLEncoder.encode(phone,"utf-8")+"&pass=" + URLEncoder.encode(pass,"utf-8");
                } catch (UnsupportedEncodingException e) {
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

                    System.out.println("result :  " + sBuffer.toString());
                    Log.i("----------------sb",sBuffer.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }



                if( Analysis(sBuffer.toString()).equals("success") )
                {

                    SharedPreferences preferen = getSharedPreferences("user",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor  editor = preferen.edit();
                    editor.putString("phone",phone);
                    editor.putString("pass",pass);
                    editor.commit();

                    Toast.makeText(getApplicationContext(),"修改密码成功",Toast.LENGTH_LONG).show();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProBar.setVisibility(View.GONE);
                            Intent intent = new Intent(ResetPassActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                else {
                    mProBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"修改密码失败",Toast.LENGTH_LONG).show();
                }

            }
        }
    };
    public String Analysis(String jsonStr){
        String states;
        String message = null ;

        //{"states":"1","message":"success"}
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            states = jsonObj.getString("states");
            message = jsonObj.getString("message");

            Log.i("--states---",states);
            Log.i("--message---",message + "\t" + message.length());

        } catch (JSONException e) {
            System.out.println("Json parse error");
            e.printStackTrace();
        }
        return message;
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
