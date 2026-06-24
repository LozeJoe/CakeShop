package com.javaBean;

import lombok.Data;


/**
 * 骑手消息实体类。
 */
@Data
public class RiderMessage {
    int id;
    int riderId;
    String type;       // order / system / income
    String title;
    String content;
    int isRead;        // 0=未读 1=已读
    String createTime;
}
