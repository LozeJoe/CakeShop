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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AdminController 测试")
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;
    @MockBean private GoodsService goodsService;
    @MockBean private OrderService orderService;
    @MockBean private TypeService typeService;
    @MockBean private AdminLogService adminLogService;
    @MockBean private CartService cartService;
    @MockBean private com.config.SystemConfigService systemConfigService;

    private final User testAdmin = TestBeans.createTestAdmin();
    private final User testUser = TestBeans.createTestUser();

    @Test @DisplayName("管理员访问成功")
    void adminIndex() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testAdmin);
        when(userService.getUserCount()).thenReturn(10);
        when(goodsService.getGoodsCount()).thenReturn(20);
        when(orderService.getOrderCount()).thenReturn(30);
        when(typeService.getTypeCount()).thenReturn(10);
        when(goodsService.getLowStockCount(5)).thenReturn(2);
        when(orderService.getFilteredOrdersPage(0, null, 1, 5)).thenReturn(new com.javaBean.PageResult<>());
        when(orderService.getOrderStatusDistribution()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/admin/index").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test @DisplayName("非管理员被拦截")
    void adminIndexNotAdmin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        mockMvc.perform(get("/admin/index").session(session))
                .andExpect(status().isForbidden());
    }

    @Test @DisplayName("用户管理")
    void adminUsers() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testAdmin);
        when(userService.getUserByPage(1, 10)).thenReturn(new com.javaBean.PageResult<>());
        when(typeService.getAllTypes()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/admin/users").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/userList"));
    }

    @Test @DisplayName("商品管理")
    void adminGoods() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testAdmin);
        when(goodsService.getGoodsByPage(1, 5)).thenReturn(new com.javaBean.PageResult<>());
        when(typeService.getAllTypes()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/admin/goods").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/goodsList"));
    }

    @Test @DisplayName("订单管理")
    void adminOrders() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testAdmin);
        when(orderService.getFilteredOrdersPage(0, null, 1, 5)).thenReturn(new com.javaBean.PageResult<>());
        when(typeService.getAllTypes()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/admin/orders").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/orderList"));
    }
}
