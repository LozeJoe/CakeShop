package com.example.demo.unit.service;

import com.example.demo.config.TestBeans;
import com.javaBean.*;
import com.mapper.*;
import com.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 单元测试")
class OrderServiceUnitTest {

    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private CartMapper cartMapper;
    @Mock private GoodsMapper goodsMapper;

    @InjectMocks private OrderServiceImpl orderService;

    private User testUser;
    private Cart testCart;
    private Goods testGoods;

    @BeforeEach
    void setUp() {
        testUser = TestBeans.createTestUser();
        testCart = new Cart();
        testCart.setGoodId("1");
        testCart.setAmount(2);
        testCart.setPrice(100.0);
        testCart.setTotalPrice(200.0);
        testCart.setUserName("testuser");
        testGoods = TestBeans.createTestGoods(1);
    }

    @Test @DisplayName("从购物车创建订单")
    void createOrderFromCart() {
        when(goodsMapper.getGoodsById(1)).thenReturn(testGoods);
        doNothing().when(orderMapper).addOrder(any(Order.class));
        doNothing().when(orderItemMapper).addOrderItem(any(OrderItem.class));
        when(cartMapper.clearCart("testuser")).thenReturn(1);

        Order result = orderService.createOrderFromCart(testUser, Arrays.asList(testCart),
                "测试用户", "13800138000", "测试地址", 1);
        assertNotNull(result);
        assertNotNull(result.getId());
        verify(orderMapper).addOrder(any(Order.class));
        verify(cartMapper).clearCart("testuser");
    }

    @Test @DisplayName("创建订单 - 购物车为空抛出异常")
    void createOrderFromCartEmpty() {
        assertThrows(RuntimeException.class, () ->
                orderService.createOrderFromCart(testUser, Collections.emptyList(),
                        "测试用户", "13800138000", "测试地址", 1));
    }

    @Test @DisplayName("创建订单 - 库存不足抛出异常")
    void createOrderFromCartStockShortage() {
        testGoods.setStock(1);
        when(goodsMapper.getGoodsById(1)).thenReturn(testGoods);
        assertThrows(RuntimeException.class, () ->
                orderService.createOrderFromCart(testUser, Arrays.asList(testCart),
                        "测试用户", "13800138000", "测试地址", 1));
    }

    @Test @DisplayName("取消订单 - 恢复库存")
    void cancelOrderRestoreStock() {
        Order order = TestBeans.createTestOrder();
        order.setStatus(1);
        when(orderMapper.getOrderById("ORDER001")).thenReturn(order);
        OrderItem item = new OrderItem();
        item.setGoodsId(1);
        item.setAmount(2);
        when(orderItemMapper.getOrderItemsByOrderId("ORDER001")).thenReturn(Arrays.asList(item));

        orderService.cancelOrder("ORDER001");
        verify(orderMapper).updateOrderStatus("ORDER001", 6);
        verify(goodsMapper).restoreStock(1, 2);
    }

    @Test @DisplayName("获取过滤订单列表")
    void getFilteredOrdersPage() {
        when(orderMapper.getFilteredOrders(0, null, 0, 10)).thenReturn(Arrays.asList(TestBeans.createTestOrder()));
        when(orderMapper.getFilteredOrdersCount(0, null)).thenReturn(1);
        var result = orderService.getFilteredOrdersPage(0, null, 1, 10);
        assertEquals(1, result.getData().size());
    }

    @Test @DisplayName("获取订单详情")
    void getOrderById() {
        when(orderMapper.getOrderById("ORDER001")).thenReturn(TestBeans.createTestOrder());
        assertNotNull(orderService.getOrderById("ORDER001"));
    }
}
