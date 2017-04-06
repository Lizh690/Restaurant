package com.lee.restaurant.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.lee.restaurant.Fragment.PastFragment;
import com.lee.restaurant.Model.PastDataList;
import com.lee.restaurant.R;

import butterknife.InjectView;

/**
 * Created by ZHB on 2016/10/13.
 */

public class PastDetialActivity extends BaseActivity{

    @InjectView(R.id.pastdetial_rv)
    RecyclerView recyclerView;

    @InjectView(R.id.pastdetial_tv_id)
    TextView textViewid;

    @InjectView(R.id.pastdetial_tv_time)
    TextView textViewtime;

    @InjectView(R.id.pastdetial_tv_pay)
    TextView textViewpay;

    @InjectView(R.id.pastdetial_tv_sum)
    TextView textViewsum;

    @InjectView(R.id.pastdetial_tv_tips)
    TextView textViewtips;

    @InjectView(R.id.pastDetial_tv_state)
    TextView textViewstate;

    private PastDataList data;
    private PastAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pastdetial_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("订单详情");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PastDetialActivity.this,PastActivity.class);
                startActivity(intent);
                finish();
            }
        });

        data = (PastDataList) getIntent().getSerializableExtra(PastFragment.ARG_ARTICLE);
        Log.i("data--->", String.valueOf(data.getDishes()));
        init();
    }

    private void init() {
        textViewid.setText("订单号： " + data.getOid().toString());
        textViewpay.setText("支付方式： " + data.getOpay().toString());
        textViewstate.setText("完成状态： " + data.getOstate().toString());
        textViewsum.setText("总金额： " + data.getOsum().toString());
        textViewtime.setText("订单时间： " + data.getOtime().toString());
        textViewtips.setText("备注： " + data.getTips().toString());

        recyclerView.setAdapter(adapter = new PastAdapter(getApplicationContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerDecoration decoration = new DividerDecoration(Color.BLUE,2);
        recyclerView.addItemDecoration(decoration);

        for (int i = 0;i < data.getDishes().size(); i ++) {
            adapter.add(data.getDishes().get(i));
        }

    }

    public class PastAdapter extends RecyclerArrayAdapter<PastDataList.DishesBeanlist> {

        public PastAdapter(Context context){
            super(context);
        }

        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return  new PastViewHolder(parent);
        }

        public class PastViewHolder extends BaseViewHolder<PastDataList.DishesBeanlist>{

            private  ImageView imageView1;
            private TextView textViewname1;
            private TextView textViewnum1;

            public PastViewHolder(ViewGroup itemView) {
                super(itemView, R.layout.pastdetial_item);
                imageView1 = $(R.id.pastdetial_item_image);
                textViewname1 = $(R.id.pastdetial_item_name);
                textViewnum1 = $(R.id.pastdetial_item_num);
            }

            @Override
            public void setData(PastDataList.DishesBeanlist datas) {
                super.setData(datas);
                textViewname1.setText(datas.getName());
                textViewnum1.setText(datas.getNum());
                Glide.with(getContext())
                        .load(datas.getPicture())
                        .placeholder(R.mipmap.icon)
                        .centerCrop()
                        .into(imageView1);
            }
        }

    }
}
