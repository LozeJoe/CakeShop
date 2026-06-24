package com.javaBean;

import lombok.Data;


/**
 * 订单实体类。
 */
@Data
public class Order {
    String id;
    double total;
    int amount;
    int status;
    int paytype;
    String name;
    String phone;
    String address;
    String datetime;
    String deliveryTime;  // 期望送达时间
    double latitude;      // 收货地址纬度
    double longitude;     // 收货地址经度
    double commission;    // 管理员设置的佣金（配送费）
    int reviewRating;     // 订单评价评分 1-5
    String reviewContent; // 订单评价内容
    int userId;
    int riderId;          // 骑手ID
    double riderIncome;   // 实际配送费收入
}
