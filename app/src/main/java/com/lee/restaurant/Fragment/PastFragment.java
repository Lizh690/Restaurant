package com.lee.restaurant.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.restaurant.Activity.PastActivity;
import com.lee.restaurant.Activity.PastDetialActivity;
import com.lee.restaurant.Model.PastData;
import com.lee.restaurant.Model.PastDataList;
import com.lee.restaurant.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 历史账单
 * <p>
 * Created by ZHB on 2016/10/8.
 */

public class PastFragment extends Fragment {


    public static final String ARG_ARTICLE = "OK";
    private RecyclerView mRecyclerView;

    private HomeAdapter mAdapter;
    private List<PastDataList> pastdatas;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.past_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.past_rv_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter = new HomeAdapter());

        pastdatas = new ArrayList<>(PastActivity.pastdatas);

        return view;
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    getActivity()).inflate(R.layout.item_past, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            holder.textViewoid.setText(pastdatas.get(position).getOid());
            holder.textViewostate.setText(pastdatas.get(position).getOstate());
            holder.textViewotime.setText(pastdatas.get(position).getOtime());
            holder.textViewosum.setText("￥ "+pastdatas.get(position).getOsum());

            holder.itemView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ARG_ARTICLE,pastdatas.get(position));

                    Intent intent = new Intent(getActivity(), PastDetialActivity.class);
                    intent.putExtra(ARG_ARTICLE,pastdatas.get(position));
                    getActivity().startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return pastdatas.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView textViewoid;
            TextView textViewosum;
            TextView textViewotime;
            TextView textViewostate;
            //TextView tv;

            public MyViewHolder(View view) {
                super(view);

                imageView = (ImageView) view.findViewById(R.id.past_item_iv);
                textViewoid = (TextView) view.findViewById(R.id.past_tv_id);
                textViewostate = (TextView) view.findViewById(R.id.past_tv_state);
                textViewosum = (TextView)view.findViewById(R.id.past_tv_pay);
                textViewotime = (TextView)view.findViewById(R.id.past_tv_time);
            }
        }
    }
}
