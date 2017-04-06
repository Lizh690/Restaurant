package com.lee.restaurant.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lee on 2016/9/17.
 */
public class OrderData {

    @SerializedName("data")
    public List<OrderResult> orders;

}
