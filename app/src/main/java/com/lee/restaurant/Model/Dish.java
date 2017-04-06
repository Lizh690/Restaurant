package com.lee.restaurant.Model;

/**
 * Created by Lee on 2016/9/17.
 */
public class Dish {

    String name;
    String price;
    String path;
    String no;
    String info;
    int count;

    public Dish(String name, String price, String path, String no, String info) {
        this.name = name;
        this.price = price;
        this.path = path;
        this.no = no;
        this.info = info;
        count = 0;
    }

    public Dish(Dishes.KindEntity.DishEntity dishEntity){
        this.name = dishEntity.name;
        this.price = dishEntity.price;
        this.path = dishEntity.path;
        this.no = dishEntity.no;
        this.info = dishEntity.info;
        count = 0;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
