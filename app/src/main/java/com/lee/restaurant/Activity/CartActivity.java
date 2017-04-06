package com.lee.restaurant.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.lee.restaurant.Fragment.CartFragment;
import com.lee.restaurant.Fragment.CartNoneFragment;
import com.lee.restaurant.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Lee on 2016/9/10.
 */
public class CartActivity extends BaseActivity {

    CartFragment cartFragment;
    CartNoneFragment cartNoneFragment;

    FragmentManager fm;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setTitle("购物车");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fm = getFragmentManager();
        transaction = fm.beginTransaction();

        if (MainActivity.cart.size() == 0){
            cartNoneFragment = new CartNoneFragment();
            transaction.replace(R.id.cart_frame_layout, cartNoneFragment);
        }else{
            cartFragment = new CartFragment();
            transaction.replace(R.id.cart_frame_layout, cartFragment);
        }
        transaction.commit();

    }

    public void updateToCartNoneFragment(){
        if (fm == null)
            fm = getFragmentManager();
        transaction = fm.beginTransaction();
        cartNoneFragment = new CartNoneFragment();
        transaction.replace(R.id.cart_frame_layout, cartNoneFragment);
        transaction.commit();
    }
}
