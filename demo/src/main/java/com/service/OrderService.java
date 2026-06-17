package com.service;

import com.javaBean.Cart;
import com.javaBean.Order;
import com.javaBean.OrderItem;
import com.javaBean.PageResult;
import com.javaBean.User;

import java.util.List;

public interface OrderService {
    void addOrder(Order order);
    List<Order> getOrdersByUserId(int userId);
    PageResult<Order> getOrdersByUserIdPage(int userId, int pageNum, int pageSize);
    Order getOrderById(String id);
    void updateOrderStatus(String id, int status);
    void cancelOrder(String id);
    void addOrderItem(OrderItem orderItem);
    List<OrderItem> getOrderItemsByOrderId(String orderId);
    List<Order> getAllOrders();
    PageResult<Order> getOrdersByPage(int pageNum, int pageSize);
    int getOrderCount();
    PageResult<Order> searchOrdersByUserId(int userId, String keyword, int pageNum, int pageSize);
    PageResult<Order> getFilteredOrdersPage(int status, String keyword, int pageNum, int pageSize);
    java.util.List<java.util.Map<String, Object>> getOrderStatusDistribution();
    Order createOrderFromCart(User user, List<Cart> cartList, String name, String phone, String address, int paytype);
    Order createOrderFromCart(User user, List<Cart> cartList, String name, String phone, String address, int paytype, String deliveryTime, double latitude, double longitude);
    void setCommission(String orderId, double commission);
    void setDeliveryTime(String orderId, String deliveryTime);
    void setReview(String orderId, int rating, String content);
    // 骑手流程状态转移（带状态机校验）
    void acceptOrder(String orderId, int riderId);
    void startDelivery(String orderId, int riderId);
    void completeDelivery(String orderId, int riderId, double income);
}
