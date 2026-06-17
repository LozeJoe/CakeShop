package com.example.demo.controller;

import com.example.demo.config.TestBeans;
import com.javaBean.User;
import com.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("OrderController 测试")
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private OrderService orderService;
    @MockBean private CartService cartService;
    @MockBean private UserService userService;
    @MockBean private TypeService typeService;

    private final User testUser = TestBeans.createTestUser();

    @Test @DisplayName("未登录跳转")
    void orderListRequiresLogin() throws Exception {
        mockMvc.perform(get("/order/orderList"))
                .andExpect(status().is3xxRedirection());
    }

    @Test @DisplayName("已查看订单")
    void myOrder() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        when(orderService.getOrdersByUserIdPage(1, 1, 5)).thenReturn(new com.javaBean.PageResult<>());
        when(typeService.getAllTypes()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/order/myOrder").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("myOrder"));
    }

    @Test @DisplayName("订单详情")
    void orderDetail() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        when(orderService.getOrderById("ORDER001")).thenReturn(TestBeans.createTestOrder());
        mockMvc.perform(get("/order/detail").param("orderId", "ORDER001").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("orderDetail"));
    }

    @Test @DisplayName("取消订单")
    void cancelOrder() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        mockMvc.perform(get("/order/cancelOrder").param("orderId", "ORDER001").session(session))
                .andExpect(status().is3xxRedirection());
    }
}
