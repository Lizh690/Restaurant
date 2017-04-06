package com.lee.restaurant.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.restaurant.R;

import butterknife.InjectView;

/**
 *  更改用户信息界面
 *
 * Created by ZHB on 2016/10/2.
 */

public class ChangeActivity extends BaseActivity implements View.OnClickListener{

    @InjectView(R.id.chageuser_rl_phone)
    RelativeLayout relativeLayoutPhone;

    @InjectView(R.id.chageuser_rl_pwd)
    RelativeLayout relativeLayoutPwd;

    @InjectView(R.id.chageuser_tv_name)
    EditText  edittextName;

    @InjectView(R.id.chageuser_tv_phone)
    TextView textViewPhone;

    @InjectView(R.id.chageuser_tv_pwd)
    TextView textViewPwd;

    @InjectView(R.id.chageuser_et_adress)
    EditText editTextAdress;

    @InjectView(R.id.chageuser_btn_ok)
    Button buttonOK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.changeuser_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("更改个人信息");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        SharedPreferences preferences = getSharedPreferences("user",Activity.MODE_PRIVATE);
        String name = preferences.getString("name","");
        String pass = preferences.getString("pass","");
        String phone = preferences.getString("phone","");
        String adress = preferences.getString("address","");
        if(! name.equals("") && ! pass.equals("") && ! phone.equals("") && ! adress.equals(""))
        {
            edittextName.setText( name);
            editTextAdress.setText(adress);
            textViewPhone.setText(phone);
            textViewPwd.setText(pass);
        }

        relativeLayoutPhone.setOnClickListener(this);
        relativeLayoutPwd.setOnClickListener(this);
        buttonOK.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.chageuser_rl_phone:
                //Toast.makeText(getApplicationContext(),"更改电话",Toast.LENGTH_SHORT).show();

                ChagePhone();

                break;
            case R.id.chageuser_rl_pwd:
                Toast.makeText(getApplicationContext(),"更改密码暂未实现",Toast.LENGTH_SHORT).show();

                ChagePaddword();

                break;
            case R.id.chageuser_btn_ok:
                CommitInfo();

            default:
                break;
        }
    }

    /**
     * 保存信息
     */
    private void CommitInfo() {

        SharedPreferences preference = getSharedPreferences("user",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("name",edittextName.getText().toString());
        editor.putString("phone",textViewPhone.getText().toString());
        editor.putString("address",editTextAdress.getText().toString());
        editor.putString("pass",textViewPwd.getText().toString());
        editor.commit();
        finish();
        Toast.makeText(getApplication(),"更改成功",Toast.LENGTH_SHORT);
    }

    private void ChagePaddword() {


    }

    /**
     * 更改电话号码，启动修改界面
     */
    private void ChagePhone() {
        Intent intent = new Intent(ChangeActivity.this,LostPassSMSActivity.class);
        intent.putExtra("activity","change");
        startActivityForResult(intent,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1)
        {

            if(resultCode == LostPassSMSActivity.RESULT_CODE)
            {
                Bundle bundle = data.getExtras();
                String str = bundle.getString("back");
                //取回值
                if(str.toString().equals("sucess"))
                {
                    textViewPhone.setText(bundle.getString("phone"));

                }else{
                    //textViewPhone.setText();
                    Toast.makeText(getApplicationContext(),"验证失败",Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
