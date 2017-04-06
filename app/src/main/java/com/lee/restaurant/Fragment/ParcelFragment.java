package com.lee.restaurant.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lee.restaurant.Activity.BalanceActivity;
import com.lee.restaurant.Activity.MainActivity;
import com.lee.restaurant.Model.Dish;
import com.lee.restaurant.Model.Dishes;
import com.lee.restaurant.Model.OrderResult;
import com.lee.restaurant.Model.TakeOutOrder;
import com.lee.restaurant.R;
import com.lee.restaurant.Service.DataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Subscriber;

/**
 * Created by ZHB on 2016/10/4.
 */

public class ParcelFragment extends Fragment implements View.OnClickListener{

    @InjectView(R.id.parcel_rl_pay)
    RelativeLayout relativeLayoutpay;
    @InjectView(R.id.parcel_rl_invoice)
    RelativeLayout relativeLayoutinvoice;

    @InjectView(R.id.parcel_et_name)
    EditText editTextname;

    @InjectView(R.id.parcel_et_address)
    EditText editTextaddress;

    @InjectView(R.id.parcel_et_phone)
    TextView editTextphone;

    @InjectView(R.id.parcel_et_mark)
    EditText editTextremark;

    @InjectView(R.id.parcel_tv_pay)
    TextView textViewpay;

    @InjectView(R.id.parcel_tv_invoince)
    TextView textViewinvoince;

    @InjectView(R.id.parcel_tv_total)
    TextView textViewtotal;

    @InjectView(R.id.parcel_btn_pay)
    Button buttonpay;

    BalanceActivity activity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parcel_fragment,null);
        ButterKnife.inject(this,view);

        activity = (BalanceActivity) getActivity();

        relativeLayoutinvoice.setOnClickListener(this);
        relativeLayoutpay.setOnClickListener(this);
        buttonpay.setOnClickListener(this);

        ((BalanceActivity) getActivity()).getSupportActionBar().setTitle("结算");

        SharedPreferences preferences = getActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
        if(!preferences.getString("phone","").equals(""))
        {
            editTextname.setText(preferences.getString("name",""));
            editTextphone.setText(preferences.getString("phone",""));
            editTextaddress.setText(preferences.getString("address",""));
        }

        textViewtotal.setText("待支付:" + getArguments().getString("totalPrice"));

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.parcel_rl_pay: // 支付方式
                showDialogPay();
                break;
            case R.id.parcel_rl_invoice: // 开发票
                showDialogInvoince();

                break;
            case R.id.parcel_btn_pay:

                submitOrder();
                break;
        }
    }

    public void submitOrder(){
        final TakeOutOrder order = setupOrderData(MainActivity.cart);
        String json = new Gson().toJson(order);

        Log.i("------------->json",json.toString());

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),json);
        Subscriber<OrderResult> subscriber = new Subscriber<OrderResult>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.i("----------------",e.getMessage());
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(OrderResult orderResult) {
                if (orderResult.status == 1){
                    activity.updateResultFragment(orderResult);
                    MainActivity.cart.clear();
                    MainActivity.cartList.clear();
                    MainActivity.QRorNot = false;
                    Log.i("-------------->","提交成功");
                    Log.i("---message---",orderResult.message);
                }else{
                    Toast.makeText(getActivity(),"提交失败" + orderResult.message.toString(),Toast.LENGTH_SHORT).show();
                    Log.i("-------------->","提交失败" + orderResult.message.toString());
                }
            }
        };
        DataService.getInstance().getPostOrderResult(subscriber,requestBody);

        /*String url = "http://123.206.221.174/app/user_order.php";
        postJson(url,json);*/
    }


    private void postJson(String url,String json){
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("---------",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("---------",response.body().string());
            }
        });
    }

    private void showDialogPay(){
        View dialogPay = getActivity().getLayoutInflater().inflate(R.layout.parcel_pay,null);
        final RadioGroup radioGroup = (RadioGroup) dialogPay.findViewById(R.id.parcel_pay_radio_group);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("支付方式");
        builder.setView(dialogPay);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.parcel_pay_rb_online:
                        textViewpay.setText("在线支付");
                        dialogInterface.dismiss();
                        break;
                    case R.id.parcel_pay_rb_after:
                        textViewpay.setText("货到付款");
                        dialogInterface.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    private void showDialogInvoince(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("是否开具发票");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textViewinvoince.setText("否");
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textViewinvoince.setText("是");
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public TakeOutOrder setupOrderData(Map<String,Dish> cart){

        List<TakeOutOrder.DishesBean> list = new ArrayList<>();
        double totalPrice = 0;

        TakeOutOrder order = new TakeOutOrder();
        order.setName(editTextname.getText().toString().trim());
        order.setAddress(editTextaddress.getText().toString().trim());
        order.setUid(editTextphone.getText().toString().trim());
        order.setTips(editTextremark.getText().toString().trim() );

        for(Dish dish:cart.values()){
            TakeOutOrder.DishesBean bean = new TakeOutOrder.DishesBean();
            bean.setCount("" + dish.getCount());
            bean.setNo(dish.getNo());
            totalPrice += Double.valueOf(dish.getPrice()) * dish.getCount();
            list.add(bean);
        }

        switch (textViewpay.getText().toString()) {
            case "在线支付":
                order.setPay(4);
                break;
            case "货到付款":
                order.setPay(5);
                break;
        }
        order.setOsum(""+totalPrice);
        order.setDishes(list);
        return order;
    }
}
