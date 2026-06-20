package com.service;

import com.javaBean.*;
import com.mapper.CartMapper;
import com.mapper.GoodsMapper;
import com.mapper.OrderItemMapper;
import com.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    // ===== 订单状态机定义 =====
    // 状态常量
    public static final int STATUS_UNPAID    = 1;  // 待支付
    public static final int STATUS_PAID      = 2;  // 待配送/已支付
    public static final int STATUS_PICKUP    = 3;  // 待取货(骑手已接单)
    public static final int STATUS_DELIVERING= 4;  // 配送中
    public static final int STATUS_DELIVERED = 5;  // 已送达
    public static final int STATUS_CANCELLED = 6;  // 已取消

    // 状态名称
    public static final String[] STATUS_NAMES = {"", "待支付", "待配送", "待取货", "配送中", "已送达", "已取消"};

    // 校验状态值的合法性
    private void validateStatusCode(int status) {
        if (status < 1 || status > 6) {
            throw new RuntimeException("非法状态码: " + status);
        }
    }

    // 状态机：定义允许的自动流转（用户/骑手正常流程）
    private static final java.util.Map<Integer, java.util.Set<Integer>> USER_TRANSITIONS = new java.util.HashMap<>();
    static {
        USER_TRANSITIONS.put(STATUS_UNPAID,     new java.util.HashSet<>(java.util.Arrays.asList(STATUS_PAID, STATUS_CANCELLED)));
        USER_TRANSITIONS.put(STATUS_PAID,       new java.util.HashSet<>(java.util.Arrays.asList(STATUS_PICKUP, STATUS_CANCELLED)));
        USER_TRANSITIONS.put(STATUS_PICKUP,     new java.util.HashSet<>(java.util.Arrays.asList(STATUS_DELIVERING, STATUS_CANCELLED)));
        USER_TRANSITIONS.put(STATUS_DELIVERING, new java.util.HashSet<>(java.util.Arrays.asList(STATUS_DELIVERED)));
        USER_TRANSITIONS.put(STATUS_DELIVERED,  new java.util.HashSet<>());  // 终态
        USER_TRANSITIONS.put(STATUS_CANCELLED,  new java.util.HashSet<>());  // 终态
    }

    // 管理员允许的流转（可强制向前流转 + 取消，但不能回退或修改终态）
    private static final java.util.Map<Integer, java.util.Set<Integer>> ADMIN_TRANSITIONS = new java.util.HashMap<>();
    static {
        ADMIN_TRANSITIONS.put(STATUS_UNPAID,     new java.util.HashSet<>(java.util.Arrays.asList(STATUS_PAID, STATUS_PICKUP, STATUS_DELIVERING, STATUS_DELIVERED, STATUS_CANCELLED)));
        ADMIN_TRANSITIONS.put(STATUS_PAID,       new java.util.HashSet<>(java.util.Arrays.asList(STATUS_PICKUP, STATUS_DELIVERING, STATUS_DELIVERED, STATUS_CANCELLED)));
        ADMIN_TRANSITIONS.put(STATUS_PICKUP,     new java.util.HashSet<>(java.util.Arrays.asList(STATUS_DELIVERING, STATUS_DELIVERED, STATUS_CANCELLED)));
        ADMIN_TRANSITIONS.put(STATUS_DELIVERING, new java.util.HashSet<>(java.util.Arrays.asList(STATUS_DELIVERED, STATUS_CANCELLED)));
        ADMIN_TRANSITIONS.put(STATUS_DELIVERED,  new java.util.HashSet<>());
        ADMIN_TRANSITIONS.put(STATUS_CANCELLED,  new java.util.HashSet<>());
    }

    // 校验状态流转合法性
    private void validateTransition(int currentStatus, int newStatus, boolean isAdmin) {
        validateStatusCode(currentStatus);
        validateStatusCode(newStatus);

        if (currentStatus == newStatus) {
            throw new RuntimeException("状态未发生变化");
        }

        java.util.Set<Integer> allowed = isAdmin
            ? ADMIN_TRANSITIONS.get(currentStatus)
            : USER_TRANSITIONS.get(currentStatus);

        if (allowed == null || !allowed.contains(newStatus)) {
            String fromName = STATUS_NAMES[currentStatus];
            String toName = STATUS_NAMES[newStatus];
            throw new RuntimeException(
                String.format("非法订单状态流转: 不允许从「%s」直接跳转到「%s」", fromName, toName)
            );
        }
    }

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private CartMapper cartMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrderFromCart(User user, List<Cart> cartList, String name, String phone, String address, int paytype) {
        return createOrderFromCart(user, cartList, name, phone, address, paytype, null, 0, 0);
    }

    public Order createOrderFromCart(User user, List<Cart> cartList, String name, String phone, String address, int paytype, String deliveryTime, double latitude, double longitude) {
        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("购物车为空，无法创建订单");
        }
        double total = 0;
        int amount = 0;
        for (Cart cart : cartList) {
            int goodsId = Integer.parseInt(cart.getGoodId());
            Goods goods = goodsMapper.getGoodsById(goodsId);
            if (goods == null) throw new RuntimeException("商品不存在: " + cart.getGoodId());
            if (goods.getStock() < cart.getAmount())
                throw new RuntimeException("商品 [" + goods.getName() + "] 库存不足");
            total += cart.getTotalPrice();
            amount += cart.getAmount();
        }
        String orderId = String.valueOf(System.currentTimeMillis());
        Order order = new Order();
        order.setId(orderId); order.setTotal(total); order.setAmount(amount);
        order.setStatus(paytype > 0 ? 2 : 1); order.setPaytype(paytype);
        order.setName(name != null && !name.isEmpty() ? name : user.getUsername());
        order.setPhone(phone != null ? phone : "");
        order.setAddress(address != null ? address : "");
        order.setDatetime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        order.setDeliveryTime(deliveryTime != null ? deliveryTime : "");
        order.setLatitude(latitude);
        order.setLongitude(longitude);
        order.setCommission(0);
        order.setUserId(user.getId());
        orderMapper.addOrder(order);
        for (Cart cart : cartList) {
            int goodsId = Integer.parseInt(cart.getGoodId());
            goodsMapper.decreaseStock(goodsId, cart.getAmount());
            goodsMapper.increaseSales(goodsId, cart.getAmount());
            OrderItem item = new OrderItem();
            item.setPrice(cart.getPrice()); item.setAmount(cart.getAmount());
            item.setGoodsId(goodsId); item.setOrderId(orderId);
            orderItemMapper.addOrderItem(item);
        }
        cartMapper.clearCart(user.getUsername());
        return order;
    }

    @Override
    public void setCommission(String orderId, double commission) {
        orderMapper.setCommission(orderId, commission);
    }

    @Override
    public void setDeliveryTime(String orderId, String deliveryTime) {
        orderMapper.setDeliveryTime(orderId, deliveryTime);
    }

    @Override
    public void setReview(String orderId, int rating, String content) {
        orderMapper.setReview(orderId, Math.max(1, Math.min(5, rating)), content != null ? content : "");
    }

    @Override
    public void addOrder(Order order) {
        orderMapper.addOrder(order);
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        return orderMapper.getOrdersByUserId(userId);
    }

    @Override
    public PageResult<Order> getOrdersByUserIdPage(int userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> data = orderMapper.getOrdersByUserIdPage(userId, offset, pageSize);
        int totalCount = orderMapper.getOrderCountByUserId(userId);
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    @Override
    public Order getOrderById(String id) {
        return orderMapper.getOrderById(id);
    }

    @Override
    public void updateOrderStatus(String id, int newStatus) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) throw new RuntimeException("订单不存在: " + id);
        // 管理员调用，允许强制向前流转
        validateTransition(order.getStatus(), newStatus, true);
        orderMapper.updateOrderStatus(id, newStatus);
    }

    @Override
    @Transactional
    public void cancelOrder(String id) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) throw new RuntimeException("订单不存在");
        // 校验：仅非终态可取消
        validateTransition(order.getStatus(), STATUS_CANCELLED, false);
        // 取消时恢复库存
        List<OrderItem> items = orderItemMapper.getOrderItemsByOrderId(id);
        for (OrderItem item : items) {
            goodsMapper.restoreStock(item.getGoodsId(), item.getAmount());
        }
        orderMapper.updateOrderStatus(id, STATUS_CANCELLED);
    }

    // ===== 骑手流程的状态转移（带业务校验） =====

    /** 骑手接单：2(待配送) → 3(待取货)，同时分配骑手 */
    @Override
    @Transactional
    public void acceptOrder(String orderId, int riderId) {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        validateTransition(order.getStatus(), STATUS_PICKUP, false);
        orderMapper.acceptOrder(orderId, riderId);
    }

    /** 骑手开始配送：3(待取货) → 4(配送中) */
    @Override
    @Transactional
    public void startDelivery(String orderId, int riderId) {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        validateTransition(order.getStatus(), STATUS_DELIVERING, false);
        orderMapper.startDelivery(orderId, riderId);
    }

    /** 骑手确认送达：4(配送中) → 5(已送达)，记录配送费 */
    @Override
    @Transactional
    public void completeDelivery(String orderId, int riderId, double income) {
        Order order = orderMapper.getOrderById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        validateTransition(order.getStatus(), STATUS_DELIVERED, false);
        orderMapper.completeDelivery(orderId, riderId, income);
    }

    @Override
    public void addOrderItem(OrderItem orderItem) {
        orderItemMapper.addOrderItem(orderItem);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(String orderId) {
        return orderItemMapper.getOrderItemsByOrderId(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderMapper.getAllOrders();
    }

    @Override
    public PageResult<Order> getOrdersByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> data = orderMapper.getOrdersByPage(offset, pageSize);
        int totalCount = orderMapper.getOrderCount();
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    @Override
    public int getOrderCount() {
        return orderMapper.getOrderCount();
    }

    @Override
    public PageResult<Order> searchOrdersByUserId(int userId, String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> data = orderMapper.searchOrdersByUserId(userId, keyword, offset, pageSize);
        int totalCount = orderMapper.searchOrdersCountByUserId(userId, keyword);
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    @Override
    public java.util.List<java.util.Map<String, Object>> getOrderStatusDistribution() {
        return orderMapper.getOrderStatusDistribution();
    }

    @Override
    public PageResult<Order> getFilteredOrdersPage(int status, String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Order> data = orderMapper.getFilteredOrders(status, keyword, offset, pageSize);
        int totalCount = orderMapper.getFilteredOrdersCount(status, keyword);
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    @Override
    public double getTotalRevenue() { return orderMapper.getTotalRevenue(); }

    @Override
    public double getTodayRevenue() { return orderMapper.getTodayRevenue(); }

    @Override
    public int getTodayOrderCount() { return orderMapper.getTodayOrderCount(); }

    @Override
    public int getCompletedOrderCount() { return orderMapper.getCompletedOrderCount(); }

    @Override
    public int getPendingOrderCount() { return orderMapper.getPendingOrderCount(); }

    @Override
    public double getAvgOrderValue() { return orderMapper.getAvgOrderValue(); }

    @Override
    public java.util.List<java.util.Map<String, Object>> getMonthlySales(String startDate) { return orderMapper.getMonthlySales(startDate); }

    @Override
    public java.util.List<java.util.Map<String, Object>> getWeeklyOrders(String startDate) { return orderMapper.getWeeklyOrders(startDate); }

    @Override
    public java.util.List<java.util.Map<String, Object>> getRevenueLast30Days(String startDate) { return orderMapper.getRevenueLast30Days(startDate); }

    @Override
    public java.util.List<java.util.Map<String, Object>> getCategorySales() { return orderMapper.getCategorySales(); }

    @Override
    public java.util.List<java.util.Map<String, Object>> getTopGoods() { return orderMapper.getTopGoods(); }
}
