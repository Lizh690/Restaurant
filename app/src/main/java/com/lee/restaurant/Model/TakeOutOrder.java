package com.lee.restaurant.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lee on 2016/9/17.
 */
public class TakeOutOrder implements Serializable {


    private String uid; //手机号


    private String osum; //总价

    private String name;         //

    private String address;        //

    private List<DishesBean> dishes;

    private String tips;     //beizhu

    private int pay;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOsum() {
        return osum;
    }

    public void setOsum(String osum) {
        this.osum = osum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<DishesBean> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishesBean> dishes) {
        this.dishes = dishes;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public static class DishesBean implements Serializable {

        private String no;
        private String count;

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }
    }
}
