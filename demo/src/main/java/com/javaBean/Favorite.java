package com.javaBean;

import lombok.Data;

@Data
public class Favorite {
    int id;
    int userId;
    int goodsId;
    String createTime;
}
