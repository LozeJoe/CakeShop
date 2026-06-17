package com.javaBean;

import lombok.Data;

@Data
public class AdminLog {
    int id;
    String adminName;
    String action;
    String target;
    String ip;
    String createTime;
}
