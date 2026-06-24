package com.javaBean;

import lombok.Data;


/**
 * 收藏实体类。
 */
@Data
public class Favorite {
    int id;
    int userId;
    int goodsId;
    String createTime;
}
