package com.lee.restaurant.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lee.restaurant.Activity.BalanceActivity;
import com.lee.restaurant.Model.OrderResult;
import com.lee.restaurant.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 提交订单后的界面
 *
 *
 * Created by Lee on 2016/9/16.
 */
public class FragmentBalanceResult extends Fragment {

    @InjectView(R.id.result_tv_state)
    TextView textViewState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance_result,null);
        ButterKnife.inject(this,view);

        ((BalanceActivity) getActivity()).getSupportActionBar().setTitle("订单完成");
        OrderResult result = (OrderResult) getArguments().getSerializable("result");
        if (result != null){
            textViewState.setText("订单提交成功，您的订单号码为：" +result.message);
        }else {
            textViewState.setText("");
        }

        return view;
    }
}
