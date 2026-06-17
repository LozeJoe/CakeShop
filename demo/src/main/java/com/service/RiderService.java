package com.service;

import com.javaBean.Order;
import com.javaBean.PageResult;
import com.javaBean.RiderMessage;
import com.javaBean.User;
import com.mapper.OrderMapper;
import com.mapper.RiderMessageMapper;
import com.mapper.UserMapper;
import com.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@Service
public class RiderService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderService orderService;

    @Resource
    private RiderMessageMapper riderMessageMapper;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User login(String username, String password) {
        // Rider login: isadmin='2'，使用 BCrypt 验证
        User user = userMapper.getUserByName(username);
        if (user == null || !"2".equals(user.getIsadmin())) {
            return null;
        }
        // BCrypt 验证
        if (userService.matchesPassword(password, user.getPassword())) {
            return user;
        }
        // 兼容旧 MD5 密码迁移
        String md5Hash = legacyMd5(password);
        if (md5Hash.equals(user.getPassword())) {
            user.setPassword(userService.encodePassword(password));
            userMapper.updateUser(user);
            return user;
        }
        return null;
    }

    /** 兼容旧版 MD5 */
    private String legacyMd5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public User getById(int id) {
        return userMapper.getUserById(id);
    }

    public void updateProfile(User rider) {
        userMapper.updateUser(rider);
    }

    // 待接单列表
    public PageResult<Order> getPendingOrders(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> data = orderMapper.getPendingOrders(offset, pageSize);
        int total = orderMapper.getPendingCount();
        return new PageResult<>(data, pageNum, pageSize, total);
    }

    // 待取货订单 (status=3)
    public PageResult<Order> getRiderPickupOrders(int riderId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return new PageResult<>(orderMapper.getRiderPickupOrders(riderId, offset, pageSize), pageNum, pageSize, orderMapper.getRiderPickupCount(riderId));
    }
    // 配送中订单 (status=4)
    public PageResult<Order> getRiderDeliveringOrders(int riderId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return new PageResult<>(orderMapper.getRiderDeliveringOrders(riderId, offset, pageSize), pageNum, pageSize, orderMapper.getRiderDeliveringCount(riderId));
    }

    // 骑手已完成的订单
    public PageResult<Order> getRiderCompletedOrders(int riderId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> data = orderMapper.getRiderCompletedOrders(riderId, offset, pageSize);
        int total = orderMapper.getRiderCompletedCount(riderId);
        return new PageResult<>(data, pageNum, pageSize, total);
    }

    // 接单
    public void acceptOrder(String orderId, int riderId) {
        orderService.acceptOrder(orderId, riderId);
    }

    // 开始配送
    public void startDelivery(String orderId, int riderId) {
        orderService.startDelivery(orderId, riderId);
    }

    // 确认送达
    public void completeDelivery(String orderId, int riderId, double income) {
        orderService.completeDelivery(orderId, riderId, income);
        // Update rider balance
        userMapper.addBalance(riderId, income);
    }

    public double getTotalIncome(int riderId) {
        return orderMapper.getRiderTotalIncome(riderId);
    }

    public int getTotalCompletedCount(int riderId) {
        return orderMapper.getTotalCompletedCount(riderId);
    }

    public int getTodayCompletedCount(int riderId) {
        return orderMapper.getTodayCompletedCount(riderId);
    }

    public double getTodayIncome(int riderId) {
        return orderMapper.getTodayIncome(riderId);
    }

    public List<Map<String, Object>> getDailyIncomeLast7Days(int riderId) {
        return orderMapper.getDailyIncomeLast7Days(riderId);
    }

    // ===== 消息相关 =====

    public PageResult<RiderMessage> getMessages(int riderId, String type, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<RiderMessage> data = riderMessageMapper.getMessages(riderId, type, offset, pageSize);
        int total = riderMessageMapper.getMessageCount(riderId, type);
        return new PageResult<>(data, pageNum, pageSize, total);
    }

    public int getUnreadCount(int riderId) {
        return riderMessageMapper.getUnreadCount(riderId);
    }

    public void markMessageRead(int messageId) {
        riderMessageMapper.markAsRead(messageId);
    }

    public void markAllMessagesRead(int riderId) {
        riderMessageMapper.markAllAsRead(riderId);
    }
}
