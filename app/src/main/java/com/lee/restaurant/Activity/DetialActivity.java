package com.lee.restaurant.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.restaurant.Model.Dish;
import com.lee.restaurant.Model.Dishes;
import com.lee.restaurant.R;


import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Lee on 2016/9/11.
 */
public class DetialActivity extends BaseActivity implements View.OnClickListener {

    ImageView imageView;

    TextView textViewName;

    TextView textViewDesc;

    FloatingActionButton fab;


    Dishes.KindEntity.DishEntity dish = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_detial);
        imageView = (ImageView) findViewById(R.id.detial_iv);
        textViewName = (TextView) findViewById(R.id.detial_tv_name);
        textViewDesc = (TextView) findViewById(R.id.detial_tv_desc);
        fab = (FloatingActionButton) findViewById(R.id.detial_fab);

        dish = (Dishes.KindEntity.DishEntity) getIntent().getSerializableExtra("dish");

        if (dish != null){
            textViewName.setText(dish.name);
            textViewDesc.setText(dish.info);
            MainActivity.imageLoader.displayImage(dish.path,imageView,MainActivity.displayImageOptions);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fab.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.detial_fab:
                if (MainActivity.cartList.contains(dish.name)){
                    MainActivity.cart.get(dish.name).setCount(MainActivity.cart.get(dish.name).getCount() + 1);
                }else{
                    Dish temp = new Dish(dish);
                    temp.setCount(1);
                    MainActivity.cart.put(dish.name,temp);
                    MainActivity.cartList.add(dish.name);
                }
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                break;
            case -1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }else{
                    finish();
                }
                break;
        }
    }
}
