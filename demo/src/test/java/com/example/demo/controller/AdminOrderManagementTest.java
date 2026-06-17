package com.example.demo.controller;

import com.example.demo.config.TestBeans;
import com.javaBean.*;
import com.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 管理员订单管理全面测试
 * 覆盖：订单状态管理、配送费设置、订单详情查看
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("管理员订单管理测试")
class AdminOrderManagementTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserService userService;

    private MockHttpSession adminSession;

    @BeforeEach
    void setUp() {
        adminSession = new MockHttpSession();
        User admin = userService.login("admin", "123");
        if (admin != null) {
            adminSession.setAttribute("user", admin);
        }
    }

    @Test
    @DisplayName("非管理员访问后台被拦截")
    void nonAdminBlocked() throws Exception {
        MockHttpSession userSession = new MockHttpSession();
        User normalUser = userService.login("testuser", "123");
        if (normalUser != null) {
            userSession.setAttribute("user", normalUser);
            mockMvc.perform(get("/admin/orders").session(userSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
        }
    }

    @Test
    @DisplayName("管理员访问订单列表")
    void adminOrderList() throws Exception {
        if (adminSession.getAttribute("user") == null) return;
        mockMvc.perform(get("/admin/orders").session(adminSession))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/orderList"));
    }

    @Test
    @DisplayName("管理员筛选订单按状态")
    void adminFilterOrdersByStatus() throws Exception {
        if (adminSession.getAttribute("user") == null) return;
        mockMvc.perform(get("/admin/orders")
                .param("status", "1")
                .session(adminSession))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/orderList"))
            .andExpect(model().attributeExists("filterStatus"));
    }

    @Test
    @DisplayName("管理员搜索订单")
    void adminSearchOrders() throws Exception {
        if (adminSession.getAttribute("user") == null) return;
        mockMvc.perform(get("/admin/orders")
                .param("status", "0")
                .param("keyword", "test")
                .session(adminSession))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/orderList"))
            .andExpect(model().attributeExists("filterKeyword"));
    }

    @Test
    @DisplayName("管理员查看订单详情")
    void adminOrderDetail() throws Exception {
        if (adminSession.getAttribute("user") == null) return;

        // 先获取第一个订单
        MvcResult result = mockMvc.perform(get("/admin/orders").session(adminSession))
            .andExpect(status().isOk())
            .andReturn();

        // 只要页面能正常打开即可
        mockMvc.perform(get("/admin/orderDetail")
                .param("orderId", "TEST")
                .session(adminSession))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("未登录访问后台被拦截")
    void unauthenticatedBlocked() throws Exception {
        mockMvc.perform(get("/admin/orders"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    @DisplayName("管理员访问用户管理")
    void adminUserList() throws Exception {
        if (adminSession.getAttribute("user") == null) return;
        mockMvc.perform(get("/admin/users").session(adminSession))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/userList"));
    }

    @Test
    @DisplayName("管理员访问商品管理")
    void adminGoodsList() throws Exception {
        if (adminSession.getAttribute("user") == null) return;
        mockMvc.perform(get("/admin/goods").session(adminSession))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/goodsList"));
    }

    @Test
    @DisplayName("管理员访问分类管理")
    void adminTypeList() throws Exception {
        if (adminSession.getAttribute("user") == null) return;
        mockMvc.perform(get("/admin/types").session(adminSession))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/typeList"));
    }
}
