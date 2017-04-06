package com.lee.restaurant.Service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lee.restaurant.Api.GetApi;
import com.lee.restaurant.Api.PostApi;
import com.lee.restaurant.Api.PostTakeOutApi;
import com.lee.restaurant.Model.Data;
import com.lee.restaurant.Model.Dishes;
import com.lee.restaurant.Model.OrderResult;
import com.lee.restaurant.Model.OrderData;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Lee on 2016/9/15.
 */
public class DataService {

    public static final String Base_Url = "http://123.206.221.174/";

    private Retrofit retrofit;
    private GetApi getApi;
    private PostApi postApi;
    private PostTakeOutApi postTakeOutApi;

    private DataService(){

        retrofit = new Retrofit.Builder()
                .baseUrl(Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        getApi = retrofit.create(GetApi.class);

        postApi = retrofit.create(PostApi.class);

        postTakeOutApi = retrofit.create(PostTakeOutApi.class);
    }

    private static final DataService Instance = new DataService();

    public static DataService getInstance(){

        return Instance;
    }

    public void get(Subscriber<Dishes> subscriber){
        getApi.getDishes().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Data,Dishes>() {

                    @Override
                    public Dishes call(Data data) {
                        return data.dishes.get(0);
                    }
                })
                .subscribe(subscriber);
    }

    public void getOrderResult(Subscriber<OrderResult> subscriber, RequestBody body){
        postApi.submitOrder(body).subscribeOn(Schedulers.io()) //order.php
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<OrderData,OrderResult>() {
                    @Override
                    public OrderResult call(OrderData orderData) {
                        return orderData.orders.get(0);
                    }
                })
                .subscribe(subscriber);
    }
    public void getPostOrderResult(Subscriber<OrderResult> subscriber, RequestBody body){
        postTakeOutApi.submitPostOrder(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<OrderData,OrderResult>() {
                    @Override
                    public OrderResult call(OrderData orderData) {
                        return orderData.orders.get(0);
                    }
                })
                .subscribe(subscriber);
    }
}
