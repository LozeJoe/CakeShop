package com.javaBean;

import lombok.Data;


/**
 * 购物车实体类。
 */
@Data
public class Cart {
    int id;
    String goodId;
    String userName;
    String intro;
    int amount;
    double price;
    double totalPrice;
    String cover;
}