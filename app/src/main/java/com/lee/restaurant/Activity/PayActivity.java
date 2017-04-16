package com.lee.restaurant.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lee.restaurant.Fragment.FragmentPay;
import com.lee.restaurant.Fragment.FragmentPayed;
import com.lee.restaurant.R;

public class PayActivity extends BaseActivity {

    FragmentPay fragmentPay;
    FragmentPayed fragmentPayed;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentPay == null){
            fragmentPay = new FragmentPay();
            transaction.replace(R.id.pay_container,fragmentPay);
        }
        transaction.commit();


        getSupportActionBar().setTitle("支付订单");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this,CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void performToolbarClick(){
        toolbar.performClick();
    }

    public void changeFragment(boolean payedOrNot){
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (payedOrNot){
            if (fragmentPayed == null)
                fragmentPayed = new FragmentPayed();
            transaction.replace(R.id.pay_container,fragmentPayed);
        }else{
            if (fragmentPay == null)
                fragmentPay = new FragmentPay();
            transaction.replace(R.id.pay_container,fragmentPay);
        }
        transaction.commit();
    }
}
