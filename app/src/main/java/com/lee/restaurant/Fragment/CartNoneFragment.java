package com.lee.restaurant.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lee.restaurant.R;

/**
 *  购物车为空时的界面
 *
 * Created by Lee on 2016/9/11.
 */
public class CartNoneFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_none,container,false);
        return view;
    }

}
