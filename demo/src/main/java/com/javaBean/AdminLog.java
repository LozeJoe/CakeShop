package com.javaBean;

import lombok.Data;


/**
 * 管理员操作日志实体类。
 */
@Data
public class AdminLog {
    int id;
    String adminName;
    String action;
    String target;
    String ip;
    String createTime;
}
