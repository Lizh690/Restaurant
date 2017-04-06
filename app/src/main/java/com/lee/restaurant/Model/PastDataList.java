package com.lee.restaurant.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZHB on 2017/3/28.
 */

public class PastDataList implements Serializable  {


    private List<PastDataList.DishesBeanlist> dishes; //caidan

    private  String oid;     //订单号

    private String otime;    //时间

    private String osum;     //总价

    private String tips;     //beizhu

    private String opay;     //支付方式

    private String ostate;   //订单状态



    public List<PastDataList.DishesBeanlist> getDishes() {
        return dishes;
    }

    public void setDishes(List<PastDataList.DishesBeanlist> dishes) {
        this.dishes = dishes;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOtime() {
        return otime;
    }

    public void setOtime(String otime) {
        this.otime = otime;
    }

    public String getOsum() {
        return osum;
    }

    public void setOsum(String osum) {
        this.osum = osum;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getOpay() {
        return opay;
    }

    public void setOpay(String opay) {
        this.opay = opay;
    }

    public String getOstate() {
        return ostate;
    }

    public void setOstate(String ostate) {
        this.ostate = ostate;
    }

    @Override
    public String toString() {
        return "PastData{" +
                "dishes=" + dishes +
                ", oid='" + oid + '\'' +
                ", otime='" + otime + '\'' +
                ", osum='" + osum + '\'' +
                ", tips='" + tips + '\'' +
                ", opay='" + opay + '\'' +
                ", ostate='" + ostate + '\'' +
                '}';
    }

    public static class DishesBeanlist implements Serializable {

        private String name;
        private String num;
        private String picture;

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        @Override
        public String toString() {
            return "DishesBean{" +
                    "name='" + name + '\'' +
                    ", num='" + num + '\'' +
                    ", picture='" + picture + '\'' +
                    '}';
        }
    }
}
