package com.lee.restaurant.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lee on 2016/9/15.
 */
public class Dishes implements Serializable {

    public String status;

    public List<KindEntity> kinds;

    public static class KindEntity implements Serializable{

        public String name;

        public List<DishEntity> dishes;

        public static class DishEntity implements Serializable{

            public String name;

            public String info;

            public String price;

            public String no;

            public String path;
        }
    }
}
