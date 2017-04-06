package com.lee.restaurant.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lee on 2016/9/15.
 */
public class Data {

    @SerializedName("data")
    public List<Dishes> dishes;
}
