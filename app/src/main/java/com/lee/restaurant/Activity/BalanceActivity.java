package com.lee.restaurant.Activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.lee.restaurant.Fragment.FragmentBalance;
import com.lee.restaurant.Fragment.FragmentBalanceResult;
import com.lee.restaurant.Fragment.ParcelFragment;
import com.lee.restaurant.Model.OrderResult;
import com.lee.restaurant.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 *
 * 结算 界面
 * Created by Lee on 2016/9/15.
 */
public class BalanceActivity extends BaseActivity{

    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    FragmentBalance fragmentBalance;

    ParcelFragment parcelFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        String total = getIntent().getStringExtra("totalPrice");
        String tableNo = getIntent().getStringExtra("tableNo");

        toolbar.setTitle("结算");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.cart.isEmpty()){
                    Intent intent = new Intent(BalanceActivity.this,CartActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(BalanceActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("user", Activity.MODE_PRIVATE);
        String phone = preferences.getString("phone","");
        if(!phone.equals(""))
        {
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            parcelFragment = new ParcelFragment();
            Bundle bundle = new Bundle();
            bundle.putString("totalPrice", total);
            parcelFragment.setArguments(bundle);
            transaction.replace(R.id.balance_frame_layout, parcelFragment);
            transaction.commit();

        }else {
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            fragmentBalance = new FragmentBalance();
            Bundle bundle = new Bundle();
            bundle.putString("totalPrice", total);
            bundle.putString("tableNo", tableNo);
            fragmentBalance.setArguments(bundle);
            transaction.replace(R.id.balance_frame_layout, fragmentBalance);
            transaction.commit();
        }

    }

    public void updateResultFragment(OrderResult order){
        FragmentBalanceResult fragmentBalanceResult = new FragmentBalanceResult();
        Bundle bundle = new Bundle();
        bundle.putSerializable("result",order);
        fragmentBalanceResult.setArguments(bundle);
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.balance_frame_layout,fragmentBalanceResult);
        transaction.commit();
    }
}
