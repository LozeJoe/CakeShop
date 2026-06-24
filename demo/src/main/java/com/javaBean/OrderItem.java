package com.javaBean;

import lombok.Data;


/**
 * 订单项实体类。
 */
@Data
public class OrderItem {
    int id;
    double price;
    int amount;
    int goodsId;
    String orderId;
    String goodsName;
    String cover;
}
