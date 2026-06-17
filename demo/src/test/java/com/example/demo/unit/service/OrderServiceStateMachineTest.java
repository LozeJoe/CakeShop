package com.example.demo.unit.service;

import com.example.demo.config.TestBeans;
import com.javaBean.*;
import com.mapper.*;
import com.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单状态机全面测试
 * 覆盖：所有合法/非法流转路径、用户/管理员权限、终态保护、取消时库存恢复
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 状态机测试")
class OrderServiceStateMachineTest {

    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private CartMapper cartMapper;
    @Mock private GoodsMapper goodsMapper;

    @InjectMocks private OrderServiceImpl orderService;

    // 状态常量（与 OrderServiceImpl 保持一致）
    private static final int UNPAID     = 1;
    private static final int PAID       = 2;
    private static final int PICKUP     = 3;
    private static final int DELIVERING = 4;
    private static final int DELIVERED  = 5;
    private static final int CANCELLED  = 6;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = TestBeans.createTestOrder(); // status=1 (UNPAID)
    }

    // ========== 辅助方法 ==========
    private void mockOrderWithStatus(int status) {
        testOrder.setStatus(status);
        when(orderMapper.getOrderById("ORDER001")).thenReturn(testOrder);
    }

    // ======================================================
    // 用户正常流程：验证每个合法路径
    // ======================================================
    @Nested
    @DisplayName("用户正常流转路径")
    class UserValidTransitions {

        @Test
        @DisplayName("1→2: 用户支付成功")
        void payOrder() {
            mockOrderWithStatus(UNPAID);
            orderService.updateOrderStatus("ORDER001", PAID);
            verify(orderMapper).updateOrderStatus("ORDER001", PAID);
        }

        @Test
        @DisplayName("2→3: 骑手接单")
        void riderAccept() {
            mockOrderWithStatus(PAID);
            orderService.acceptOrder("ORDER001", 3);
            verify(orderMapper).acceptOrder("ORDER001", 3);
        }

        @Test
        @DisplayName("3→4: 骑手开始配送")
        void riderStartDelivery() {
            mockOrderWithStatus(PICKUP);
            orderService.startDelivery("ORDER001", 3);
            verify(orderMapper).startDelivery("ORDER001", 3);
        }

        @Test
        @DisplayName("4→5: 骑手完成送达")
        void riderComplete() {
            mockOrderWithStatus(DELIVERING);
            orderService.completeDelivery("ORDER001", 3, 20.0);
            verify(orderMapper).completeDelivery("ORDER001", 3, 20.0);
        }

        @Test
        @DisplayName("1→6: 用户取消未支付订单")
        void cancelUnpaidOrder() {
            mockOrderWithStatus(UNPAID);
            when(orderItemMapper.getOrderItemsByOrderId("ORDER001"))
                .thenReturn(Arrays.asList(createOrderItem(1, 2)));
            orderService.cancelOrder("ORDER001");
            verify(orderMapper).updateOrderStatus("ORDER001", CANCELLED);
            verify(goodsMapper).restoreStock(1, 2);
        }

        @Test
        @DisplayName("2→6: 取消已支付订单")
        void cancelPaidOrder() {
            mockOrderWithStatus(PAID);
            when(orderItemMapper.getOrderItemsByOrderId("ORDER001"))
                .thenReturn(Arrays.asList(createOrderItem(1, 2)));
            orderService.cancelOrder("ORDER001");
            verify(orderMapper).updateOrderStatus("ORDER001", CANCELLED);
        }

        @Test
        @DisplayName("3→6: 取消待取货订单")
        void cancelPickupOrder() {
            mockOrderWithStatus(PICKUP);
            when(orderItemMapper.getOrderItemsByOrderId("ORDER001"))
                .thenReturn(Arrays.asList(createOrderItem(1, 2)));
            orderService.cancelOrder("ORDER001");
            verify(orderMapper).updateOrderStatus("ORDER001", CANCELLED);
        }

        @Test
        @DisplayName("管理员强制送货: 1→5")
        void adminForceDeliverFromUnpaid() {
            mockOrderWithStatus(UNPAID);
            orderService.updateOrderStatus("ORDER001", DELIVERED);
            verify(orderMapper).updateOrderStatus("ORDER001", DELIVERED);
        }

        @Test
        @DisplayName("管理员强制送货: 2→5")
        void adminForceDeliverFromPaid() {
            mockOrderWithStatus(PAID);
            orderService.updateOrderStatus("ORDER001", DELIVERED);
            verify(orderMapper).updateOrderStatus("ORDER001", DELIVERED);
        }

        @Test
        @DisplayName("管理员取消配送中订单: 4→6")
        void adminCancelDelivering() {
            mockOrderWithStatus(DELIVERING);
            orderService.updateOrderStatus("ORDER001", CANCELLED);
            verify(orderMapper).updateOrderStatus("ORDER001", CANCELLED);
        }
    }

    // ======================================================
    // 用户非法路径：验证每个被拒绝的路径
    // ======================================================
    @Nested
    @DisplayName("用户非法流转路径（应拒绝）")
    class UserInvalidTransitions {

        @Test
        @DisplayName("1→3: 跳过支付直接接单 → 非法")
        void skipPayToPickup() {
            mockOrderWithStatus(UNPAID);
            assertThrows(RuntimeException.class,
                () -> orderService.acceptOrder("ORDER001", 3));
            verify(orderMapper, never()).acceptOrder(any(), anyInt());
        }

        @Test
        @DisplayName("2→4: 跳过取货直接配送 → 非法")
        void skipPickupToDelivering() {
            mockOrderWithStatus(PAID);
            assertThrows(RuntimeException.class,
                () -> orderService.startDelivery("ORDER001", 3));
            verify(orderMapper, never()).startDelivery(any(), anyInt());
        }

        @Test
        @DisplayName("3→5: 跳过配送直接送达 → 非法")
        void skipDeliveringToDelivered() {
            mockOrderWithStatus(PICKUP);
            assertThrows(RuntimeException.class,
                () -> orderService.completeDelivery("ORDER001", 3, 20.0));
            verify(orderMapper, never()).completeDelivery(any(), anyInt(), anyDouble());
        }

        @Test
        @DisplayName("1→1: 设置相同状态 → 非法")
        void sameStatus() {
            mockOrderWithStatus(UNPAID);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", UNPAID));
            verify(orderMapper, never()).updateOrderStatus(any(), anyInt());
        }

        @Test
        @DisplayName("5→2: 已送达回退到已支付 → 非法")
        void deliveredRollbackToPaid() {
            mockOrderWithStatus(DELIVERED);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", PAID));
            verify(orderMapper, never()).updateOrderStatus(any(), anyInt());
        }

        @Test
        @DisplayName("5→4: 已送达回退到配送中 → 非法")
        void deliveredRollbackToDelivering() {
            mockOrderWithStatus(DELIVERED);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", DELIVERING));
        }

        @Test
        @DisplayName("5→6: 已送达取消 → 非法")
        void deliveredCancel() {
            mockOrderWithStatus(DELIVERED);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", CANCELLED));
        }

        @Test
        @DisplayName("6→1: 已取消重新激活 → 非法")
        void cancelledReactivate() {
            mockOrderWithStatus(CANCELLED);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", UNPAID));
        }

        @Test
        @DisplayName("6→5: 已取消改成已送达 → 非法")
        void cancelledToDelivered() {
            mockOrderWithStatus(CANCELLED);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", DELIVERED));
        }

        @Test
        @DisplayName("4→2: 配送中回退到待配送 → 非法")
        void deliveringRollback() {
            mockOrderWithStatus(DELIVERING);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", PAID));
        }

        @Test
        @DisplayName("用户不能从配送中取消: 4→6 → 非法（用户路径）")
        void userCannotCancelDelivering() {
            mockOrderWithStatus(DELIVERING);
            // cancelOrder 使用 USER_TRANSITIONS，配送中不可取消
            assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder("ORDER001"));
        }

        @Test
        @DisplayName("非法状态码: status=99 → 非法")
        void invalidStatusCode() {
            mockOrderWithStatus(UNPAID);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", 99));
        }

        @Test
        @DisplayName("订单不存在 → 非法")
        void orderNotFound() {
            when(orderMapper.getOrderById("NONEXIST")).thenReturn(null);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("NONEXIST", PAID));
        }
    }

    // ======================================================
    // 管理员特殊路径（比用户更宽的权限）
    // ======================================================
    @Nested
    @DisplayName("管理员特殊流转路径")
    class AdminSpecialTransitions {

        @Test
        @DisplayName("管理员可以从1强制跳到5")
        void adminForceComplete() {
            mockOrderWithStatus(UNPAID);
            orderService.updateOrderStatus("ORDER001", DELIVERED);
            verify(orderMapper).updateOrderStatus("ORDER001", DELIVERED);
        }

        @Test
        @DisplayName("管理员可以从2强制跳到5")
        void adminForceCompleteFromPaid() {
            mockOrderWithStatus(PAID);
            orderService.updateOrderStatus("ORDER001", DELIVERED);
            verify(orderMapper).updateOrderStatus("ORDER001", DELIVERED);
        }

        @Test
        @DisplayName("管理员也不能修改已送达订单")
        void adminCannotModifyDelivered() {
            mockOrderWithStatus(DELIVERED);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", UNPAID));
        }

        @Test
        @DisplayName("管理员也不能修改已取消订单")
        void adminCannotModifyCancelled() {
            mockOrderWithStatus(CANCELLED);
            assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus("ORDER001", PAID));
        }
    }

    // ======================================================
    // 取消订单的库存恢复逻辑
    // ======================================================
    @Nested
    @DisplayName("取消订单的库存恢复")
    class CancelOrderStockRestore {

        @Test
        @DisplayName("取消订单时恢复每种商品的库存")
        void restoreMultipleItems() {
            mockOrderWithStatus(UNPAID);
            when(orderItemMapper.getOrderItemsByOrderId("ORDER001"))
                .thenReturn(Arrays.asList(
                    createOrderItem(1, 2), // goodsId=1, amount=2
                    createOrderItem(2, 1), // goodsId=2, amount=1
                    createOrderItem(3, 5)  // goodsId=3, amount=5
                ));
            orderService.cancelOrder("ORDER001");
            verify(goodsMapper).restoreStock(1, 2);
            verify(goodsMapper).restoreStock(2, 1);
            verify(goodsMapper).restoreStock(3, 5);
            verify(orderMapper).updateOrderStatus("ORDER001", CANCELLED);
        }

        @Test
        @DisplayName("取消没有订单项的订单（空订单项列表）")
        void cancelOrderWithNoItems() {
            mockOrderWithStatus(UNPAID);
            when(orderItemMapper.getOrderItemsByOrderId("ORDER001"))
                .thenReturn(Arrays.asList());
            orderService.cancelOrder("ORDER001");
            verify(goodsMapper, never()).restoreStock(anyInt(), anyInt());
            verify(orderMapper).updateOrderStatus("ORDER001", CANCELLED);
        }
    }

    // ======================================================
    // 配送费计算逻辑
    // ======================================================
    @Nested
    @DisplayName("配送费逻辑")
    class CommissionLogic {

        @Test
        @DisplayName("优先使用管理员设置的 commission")
        void useCommissionFromAdmin() {
            mockOrderWithStatus(DELIVERING);
            testOrder.setCommission(30.0);
            orderService.completeDelivery("ORDER001", 3, 30.0);
            verify(orderMapper).completeDelivery("ORDER001", 3, 30.0);
        }

        @Test
        @DisplayName("setCommission 存储配送费")
        void setCommission() {
            orderService.setCommission("ORDER001", 25.0);
            verify(orderMapper).setCommission("ORDER001", 25.0);
        }

        @Test
        @DisplayName("setReview 校验评分范围")
        void setReviewClampsRating() {
            orderService.setReview("ORDER001", 5, "好评");
            verify(orderMapper).setReview("ORDER001", 5, "好评");
        }

        @Test
        @DisplayName("setReview 过低评分被截断")
        void setReviewClampsLowRating() {
            orderService.setReview("ORDER001", 0, "差评");
            verify(orderMapper).setReview("ORDER001", 1, "差评");
        }

        @Test
        @DisplayName("setReview 过高评分被截断")
        void setReviewClampsHighRating() {
            orderService.setReview("ORDER001", 10, "超级好评");
            verify(orderMapper).setReview("ORDER001", 5, "超级好评");
        }
    }

    private OrderItem createOrderItem(int goodsId, int amount) {
        OrderItem item = new OrderItem();
        item.setGoodsId(goodsId);
        item.setAmount(amount);
        item.setPrice(100.0);
        return item;
    }
}
