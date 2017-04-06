package com.lee.restaurant.Fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lee.restaurant.Activity.BalanceActivity;
import com.lee.restaurant.Activity.MainActivity;
import com.lee.restaurant.Model.Dish;
import com.lee.restaurant.Model.Dishes;
import com.lee.restaurant.Model.Order;
import com.lee.restaurant.Model.OrderResult;
import com.lee.restaurant.R;
import com.lee.restaurant.Service.DataService;
import com.nostra13.universalimageloader.utils.L;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
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
 *   支付界面
 *
 *
 * Created by Lee on 2016/9/16.
 */
public class FragmentBalance extends Fragment implements View.OnClickListener {

    static final String URL= "http://123.206.221.174/android/order.php";

    @InjectView(R.id.balance_rl_pay)
    RelativeLayout relativeLayoutPay;

    @InjectView(R.id.balance_rl_invoice)
    RelativeLayout relativeLayoutInvoince;

    @InjectView(R.id.balance_rl_tableware)
    RelativeLayout relativeLayoutTableware;

    @InjectView(R.id.balance_btn_pay)
    Button btnPay;

    @InjectView(R.id.balance_tv_total)
    TextView textViewTotal;

    @InjectView(R.id.balance_tv_pay)
    TextView textViewPay;

    @InjectView(R.id.balance_tv_invoince)
    TextView textViewInvoince;

    @InjectView(R.id.balance_tv_tableware)
    TextView textViewTableware;

    @InjectView(R.id.balance_tv_table)
    TextView textViewTable;

    @InjectView(R.id.balance_et_remark)
    EditText editText;

    BalanceActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance,null);
        ButterKnife.inject(this,view);

        activity = (BalanceActivity) getActivity();

        relativeLayoutInvoince.setOnClickListener(this);
        relativeLayoutPay.setOnClickListener(this);
        relativeLayoutTableware.setOnClickListener(this);
        btnPay.setOnClickListener(this);

        ((BalanceActivity) getActivity()).getSupportActionBar().setTitle("结算");
        textViewTotal.setText("待支付:" + getArguments().getString("totalPrice"));
        textViewTable.setText("桌号:" + getArguments().getString("tableNo"));

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.balance_btn_pay:
                submitOrder();
                break;
            case R.id.balance_rl_invoice:
                showDialogInvoince();
                break;
            case R.id.balance_rl_pay:
                showDialogPay();
                break;
            case R.id.balance_rl_tableware:
                showDialogTableware();
                break;
        }
    }

    public void submitOrder(){
        final Order order = setupOrderData(MainActivity.cart,getArguments().getString("tableNo"));
        String json = new   Gson().toJson(order);
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),json);
        Subscriber<OrderResult> subscriber = new Subscriber<OrderResult>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(OrderResult orderResult) {
                if (orderResult.status == 1){
                    activity.updateResultFragment(orderResult);
                    MainActivity.cart.clear();
                    MainActivity.cartList.clear();
                    MainActivity.QRorNot = false;
                }else{
                    Toast.makeText(getActivity(),"提交失败" + orderResult.message,Toast.LENGTH_SHORT).show();
                }
            }
        };

        DataService.getInstance().getOrderResult(subscriber,requestBody);

        //postJson(URL,json);
    }

    private void postJson(String url,String json){
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),json);
        Request request = new Request.Builder().url(url)
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
        View dialogPay = getActivity().getLayoutInflater().inflate(R.layout.dialog_pay,null);
        final RadioGroup radioGroup = (RadioGroup) dialogPay.findViewById(R.id.dialog_pay_radio_group);
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
                    case R.id.dialog_pay_rb_wx:
                        textViewPay.setText("微信支付");
                        dialogInterface.dismiss();
                        break;
                    case R.id.dialog_pay_rb_zfb:
                        textViewPay.setText("支付宝");
                        dialogInterface.dismiss();
                        break;
                    case R.id.dialog_pay_rb_offline:
                        textViewPay.setText("前台支付");
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
                textViewInvoince.setText("否");
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textViewInvoince.setText("是");
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void showDialogTableware(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_tableware,null);
        final NumberPicker picker = (NumberPicker) view.findViewById(R.id.dialog_tableware_picker);
        picker.setMaxValue(8);
        picker.setMinValue(1);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("餐具数量");
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textViewTableware.setText("" + picker.getValue());
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public Order setupOrderData(Map<String,Dish> cart, String tableNo){
        Order order = new Order();
        order.setTableNo(tableNo);
        order.setRemark(editText.getText().toString().trim());

        List<Order.DishesBean> list = new ArrayList<>();
        double totalPrice = 0;
        for(Dish dish:cart.values()){
            Order.DishesBean bean = new Order.DishesBean();
            bean.setCount("" + dish.getCount());
            bean.setNo(dish.getNo());
            totalPrice += Double.valueOf(dish.getPrice()) * dish.getCount();
            list.add(bean);
        }

        switch (textViewPay.getText().toString()){
            case "微信支付":
                order.setPay(1);
                break;
            case "支付宝":
                order.setPay(2);
                break;
            case "前台支付":
                order.setPay(3);
                break;
        }

        if (textViewInvoince.getText().equals("是")){
            order.setBill(true);
        }else{
            order.setBill(false);
        }
        order.setTotalprice(" " + (int)totalPrice);
        order.setDishes(list);
        return order;
    }
}
