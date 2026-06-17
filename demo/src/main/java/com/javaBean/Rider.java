package com.javaBean;

import lombok.Data;

@Data
public class Rider {
    int id;
    String username;
    String password;
    String name;
    String phone;
    String idCard;     // 身份证号
    String avatar;     // 头像
    int level;         // 配送等级 1-5
    int status;        // 0=休息 1=接单中
    int totalOrders;   // 累计配送
    double totalIncome; // 累计收入
    String createTime;
}
