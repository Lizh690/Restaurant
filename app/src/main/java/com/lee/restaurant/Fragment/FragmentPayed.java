package com.lee.restaurant.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lee.restaurant.Activity.PayActivity;
import com.lee.restaurant.R;

public class FragmentPayed extends Fragment implements View.OnClickListener {

    Button btnPayed;
    PayActivity activity1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payed,container,false);

        btnPayed = (Button) view.findViewById(R.id.payed_button);
        btnPayed.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.payed_button:
                if (activity1 == null)
                    activity1 = (PayActivity) getActivity();
                activity1.performToolbarClick();
                break;
        }
    }
}
