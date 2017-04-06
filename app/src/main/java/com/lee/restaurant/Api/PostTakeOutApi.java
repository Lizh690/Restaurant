package com.lee.restaurant.Api;

import com.lee.restaurant.Model.OrderData;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ZHB on 2016/10/5.
 */

public interface PostTakeOutApi {

    @Headers({"Content-Type:application/json"})
    @POST("app/user_order.php")
    Observable<OrderData> submitPostOrder(@Body RequestBody body);

}
