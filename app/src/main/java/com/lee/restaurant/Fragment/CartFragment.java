package com.lee.restaurant.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.restaurant.Activity.BalanceActivity;
import com.lee.restaurant.Activity.CartActivity;
import com.lee.restaurant.Activity.DetialActivity;
import com.lee.restaurant.Activity.MainActivity;
import com.lee.restaurant.Model.Dish;
import com.lee.restaurant.Model.Dishes;
import com.lee.restaurant.R;

import zxing.CaptureActivity;


/**
 *  购物车 结算
 *
 *
 * Created by Lee on 2016/9/11.
 */
public class CartFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    TextView textViewTotal;
    Button btnCommit;
    CartActivity cartActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        cartActivity = (CartActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_cart,container,false);
        textViewTotal = (TextView) view.findViewById(R.id.cart_tv_total);
        btnCommit = (Button) view.findViewById(R.id.cart_btn_commit);
        btnCommit.setOnClickListener(this);

        textViewTotal.setText(updateTotalPrice());

        recyclerView = (RecyclerView) view.findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerViewAdapter());

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cart_btn_commit:
                if(IsHaveUser()){       //如果已经登录账号

                    Intent intent = new Intent(getActivity(), BalanceActivity.class);
                    intent.putExtra("totalPrice", updateTotalPrice());
                    intent.putExtra("tableNo", getActivity().getIntent().getStringExtra("tableNo"));
                    startActivity(intent);
                    getActivity().finish();

                }else
                {
                    if (!MainActivity.QRorNot) { // 未扫描二维码
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("还未扫描二维码，现在扫描吗?");
                        builder.setPositiveButton("扫描", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                                intent.putExtra("requestCode", 2);
                                startActivityForResult(intent, 2);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    } else {
                        Intent intent = new Intent(getActivity(), BalanceActivity.class);
                        intent.putExtra("totalPrice", updateTotalPrice());
                        intent.putExtra("tableNo", getActivity().getIntent().getStringExtra("tableNo"));
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
                break;
        }
    }


    /**
     *  判断当前用户是否存在
     * @return   如果存在用户，则返回true，没有用户，则返回 false
     */
    public boolean IsHaveUser()
    {

        SharedPreferences preferences = getActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);
        String phone = preferences.getString("phone","");
        if(! phone.equals(""))
        {
            return true;
        }
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 2){
            Intent intent = new Intent(getActivity(),BalanceActivity.class);
            String QRResult = data.getStringExtra("QRResult");
            if (QRResult.startsWith("http://123.206.221.174/")){
                String tableNo = QRResult.split("tableNo=")[1];
                intent.putExtra("totalPrice",updateTotalPrice());
                intent.putExtra("tableNo",tableNo);
                startActivity(intent);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("请扫描正确的二维码!");
                builder.setPositiveButton("好", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        }
    }

    private String updateTotalPrice(){
        if (!MainActivity.cart.isEmpty()){
            double total = 0;
            for (Dish dish:MainActivity.cart.values()){
                total += dish.getCount() * Float.valueOf(dish.getPrice());
            }
            java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance();
            return format.format(total);
        }
        return null;
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.cart_item_layout,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (MainActivity.cart != null){
                Dish dish = MainActivity.cart.get(MainActivity.cartList.get(position));
                MainActivity.imageLoader.displayImage(dish.getPath(),holder.imageView,MainActivity.displayImageOptions);
                holder.textViewName.setText(dish.getName());
                holder.textViewPrice.setText(dish.getPrice());
                holder.textViewCount.setText("" + dish.getCount());
                holder.textViewAdd.setTag(position);
                holder.textViewMinus.setTag(position);
            }
        }

        @Override
        public int getItemCount() {
            return MainActivity.cart.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView imageView;
            TextView textViewName,textViewPrice,textViewCount;
            TextView textViewAdd,textViewMinus;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.cart_item_iv);
                textViewName = (TextView) itemView.findViewById(R.id.cart_item_tv_name);
                textViewPrice = (TextView) itemView.findViewById(R.id.cart_item_tv_price);
                textViewCount = (TextView) itemView.findViewById(R.id.cart_tv_count);
                textViewAdd = (TextView) itemView.findViewById(R.id.cart_tv_add);
                textViewMinus = (TextView) itemView.findViewById(R.id.cart_tv_minus);

                textViewAdd.setOnClickListener(this);
                textViewMinus.setOnClickListener(this);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DetialActivity.class);
                        Dishes.KindEntity.DishEntity dishEntity = null;
                        for (int i = 0;i<MainActivity.mainData.size();i++){
                            if (MainActivity.cartList.get(getLayoutPosition()).equals(MainActivity.mainData.get(i).name)){
                                dishEntity = MainActivity.mainData.get(i);
                                break;
                            }
                        }
                        intent.putExtra("dish",dishEntity);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity(),imageView,"imageview").toBundle());
                        }else {
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                switch (view.getId()){
                    case R.id.cart_tv_add:
                        MainActivity.cart.get(MainActivity.cartList.get(position)).setCount(MainActivity.cart.get(MainActivity.cartList.get(position)).getCount() + 1);
                        notifyDataSetChanged();
                        textViewTotal.setText(updateTotalPrice());
                        break;
                    case R.id.cart_tv_minus:
                        int count = MainActivity.cart.get(MainActivity.cartList.get(position)).getCount();
                        count -=1;
                        if (count <= 0){
                            MainActivity.cart.remove(MainActivity.cartList.get(position));
                            MainActivity.cartList.remove(position);
                            if (MainActivity.cart.isEmpty()){
                                cartActivity.updateToCartNoneFragment();
                            }
                        }else{
                            MainActivity.cart.get(MainActivity.cartList.get(position)).setCount(count);
                        }
                        notifyDataSetChanged();
                        textViewTotal.setText(updateTotalPrice());
                        break;
                }
            }
        }
    }
}
