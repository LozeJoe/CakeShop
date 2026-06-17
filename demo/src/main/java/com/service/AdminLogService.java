package com.service;

import com.javaBean.AdminLog;
import com.javaBean.PageResult;
import com.mapper.AdminLogMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@EnableScheduling
public class AdminLogService {

    @Resource
    private AdminLogMapper adminLogMapper;

    public void log(String adminName, String action, String target, String ip) {
        AdminLog log = new AdminLog();
        log.setAdminName(adminName);
        log.setAction(action);
        log.setTarget(target);
        log.setIp(ip);
        adminLogMapper.addLog(log);
    }

    public PageResult<AdminLog> getLogsByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<AdminLog> data = adminLogMapper.getLogsByPage(offset, pageSize);
        int totalCount = adminLogMapper.getLogCount();
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点
    public void cleanOldLogs() {
        adminLogMapper.deleteOldLogs(30);
    }
}
