package com.lee.restaurant.Api;

import com.lee.restaurant.Model.OrderData;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Lee on 2016/9/17.
 */
public interface PostApi {

    @Headers({"Content-Type:application/json"})
    @POST("android/order.php")
    Observable<OrderData> submitOrder(@Body RequestBody body);
}
