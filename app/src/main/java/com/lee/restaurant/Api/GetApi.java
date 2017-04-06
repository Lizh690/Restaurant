package com.lee.restaurant.Api;

import com.lee.restaurant.Model.Data;
import com.lee.restaurant.Model.Dishes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Lee on 2016/9/15.
 */
public interface GetApi {

    @GET("android/get_all_menu.php")
    Observable<Data> getDishes();
}
