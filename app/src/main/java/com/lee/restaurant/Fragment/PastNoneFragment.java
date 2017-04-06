package com.lee.restaurant.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lee.restaurant.R;

/**
 *
 *  空历史账单
 * Created by ZHB on 2016/10/8.
 */

public class PastNoneFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pastnone_fragment,container,false);
        return view;

    }
}
