package com.lee.restaurant.Fragment;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.restaurant.Activity.PayActivity;
import com.lee.restaurant.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;

import c.b.BP;
import c.b.PListener;

public class FragmentPay extends Fragment implements View.OnClickListener {

    RelativeLayout payWX,payAli;
    ImageView payIVWX,payIVAli,payAliChecked,payWXChecked;
    Button payButton;
    boolean aliOrWx = true;
    ProgressDialog progressDialog;
    TextView tvTotalPrice;
    PayActivity activity1;

    String AppID = "a617ce511c1c00d781c1a8b544d25b02";
    int PlugVersion = 7;

    //商品名称
    String name = "test_name";
    //商品总价
    double totalPrice = 0.02;
    //商品描述
    String desc = "test_desc";

    private static final int REQUESTPERMISSION = 101;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay,container,false);

        BP.init(AppID);

        Intent intent = getActivity().getIntent();
        name = intent.getStringExtra("name");
        totalPrice = intent.getDoubleExtra("price",0);
        desc = intent.getStringExtra("desc");

        payWX = (RelativeLayout) view.findViewById(R.id.pay_method_wx);
        payAli = (RelativeLayout) view.findViewById(R.id.pay_method_ali);
        payIVAli = (ImageView) view.findViewById(R.id.pay_iv_ali);
        payIVWX = (ImageView) view.findViewById(R.id.pay_iv_wx);
        payAliChecked = (ImageView) view.findViewById(R.id.pay_ali_checked);
        payWXChecked = (ImageView) view.findViewById(R.id.pay_wx_checked);
        payButton = (Button) view.findViewById(R.id.pay_button);
        tvTotalPrice = (TextView) view.findViewById(R.id.pay_total);
        DecimalFormat df = new DecimalFormat("0.00");
        tvTotalPrice.setText(df.format(totalPrice));
        payAli.setOnClickListener(this);
        payWX.setOnClickListener(this);
        payButton.setOnClickListener(this);

        payAliChecked.setImageResource(R.drawable.pay_checked);
        aliOrWx = true;

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pay_button:
                pay(aliOrWx);
                break;
            case R.id.pay_method_ali:
                if (!aliOrWx){
                    payAliChecked.setImageResource(R.drawable.pay_checked);
                    payWXChecked.setImageResource(R.drawable.pay_unchecked);
                    aliOrWx = true;
                }
                break;
            case R.id.pay_method_wx:
                if (aliOrWx){
                    payWXChecked.setImageResource(R.drawable.pay_checked);
                    payAliChecked.setImageResource(R.drawable.pay_unchecked);
                    aliOrWx = false;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTPERMISSION) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    installBmobPayPlugin("bp.db");
                } else {
                    //提示没有权限，安装不了
                    Toast.makeText(getActivity(),"您拒绝了权限，这样无法安装支付插件",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void pay(boolean aliOrWx){
        if (aliOrWx){
            if (!checkPackageInstalled("com.eg.android.AlipayGphone")){
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
                dialog.setMessage("您的手机未安装支付宝，是否立即下载?");
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.alipay.com"));
                        startActivity(intent);
                        return;
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;
                    }
                });
                dialog.show();
            }
        }else{
            if (checkPackageInstalled("com.tencent.mm")){
                int pluginVersion = BP.getPluginVersion(getActivity());
                if (pluginVersion < PlugVersion) {
                    // 为0说明未安装支付插件,
                    // 否则就是支付插件的版本低于官方最新版
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
                    dialog.setMessage("检测到本机尚未安装支付插件或插件版本不是最新,无法进行支付,是否现在安装(无流量消耗)");
                    dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            installApk("bp.db");
                            return;
                        }
                    });
                    dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            return;
                        }
                    });
                    dialog.show();
                }
            }else{
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
                dialog.setMessage("您的手机未安装微信，是否立即下载?");
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://weixin.qq.com"));
                        startActivity(intent);
                        return;
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;
                    }
                });
                dialog.show();
            }
        }

        showDialog("正在获取订单...\nSDK版本号:" + BP.getPaySdkVersion());

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.bmob.app.sport",
                    "com.bmob.app.sport.wxapi.BmobActivity");
            intent.setComponent(cn);
            this.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        BP.pay(name,desc,totalPrice, aliOrWx, new PListener() {

            // 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
            @Override
            public void unknow() {
                Toast.makeText(getActivity(), "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
                        .show();
                //tv.append(name + "'s pay status is unknow\n\n");
                hideDialog();
            }

            // 支付成功,如果金额较大请手动查询确认
            @Override
            public void succeed() {
                Toast.makeText(getActivity(), "支付成功!", Toast.LENGTH_SHORT).show();
                //tv.append(name + "'s pay status is success\n\n");
                hideDialog();
                if (activity1 == null)
                    activity1 = (PayActivity) getActivity();
                activity1.changeFragment(true);
            }

            // 无论成功与否,返回订单号
            @Override
            public void orderId(String orderId) {
                // 此处应该保存订单号,比如保存进数据库等,以便以后查询
                //order.setText(orderId);
                //tv.append(name + "'s orderid is " + orderId + "\n\n");
                showDialog("获取订单成功!请等待跳转到支付页面~");
            }

            // 支付失败,原因可能是用户中断支付操作,也可能是网络原因
            @Override
            public void fail(int code, String reason) {

                // 当code为-2,意味着用户中断了操作
                // code为-3意味着没有安装BmobPlugin插件
                if (code == -3) {
                    Toast.makeText(
                            getActivity(),
                            "监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
                            Toast.LENGTH_SHORT).show();
//                    installBmobPayPlugin("bp.db");
                    installApk("bp.db");
                } else {
                    Toast.makeText(getActivity(), "支付中断! code = " + code + " reason = " + reason, Toast.LENGTH_SHORT)
                            .show();
                }
                //tv.append(name + "'s pay status is fail, error code is \n"
                //      + code + " ,reason is " + reason + "\n\n");
                hideDialog();
            }
        });
    }

    private void installApk(String s) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
        } else {
            installBmobPayPlugin(s);
        }
    }

    private void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getActivity().getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName + ".apk");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPackageInstalled(String packageName) {
        try {
            getActivity().getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    private void showDialog(String message) {
        try {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(true);
            }
            progressDialog.setMessage(message);
            progressDialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    private void hideDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
            }
    }
}
