package com.lee.restaurant.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lee on 2016/9/17.
 */
public class Order implements Serializable{

    /**
     * tableNo : string
     * dishes : [{"no":"string","count":"string"}]
     */

    private String tableNo;
    /**
     * no : string
     * count : string
     */

    private List<DishesBean> dishes;

    private String remark;

    private String totalprice;

    private int pay;

    private boolean bill;

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public boolean isBill() {
        return bill;
    }

    public void setBill(boolean bill) {
        this.bill = bill;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public List<DishesBean> getDishes() {
        return dishes;
    }

    public void setDishes(List<DishesBean> dishes) {
        this.dishes = dishes;
    }

    public static class DishesBean {
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
