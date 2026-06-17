package com.javaBean;

import lombok.Data;

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
