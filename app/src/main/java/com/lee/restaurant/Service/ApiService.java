package com.lee.restaurant.Service;

import com.lee.restaurant.Model.PastData;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ZHB on 2017/3/28.
 */

public interface ApiService {

    // 历史菜单
    @GET("history_order.php")
    Observable<PastData> GetPastData(@Query("uid") String uid);
}
