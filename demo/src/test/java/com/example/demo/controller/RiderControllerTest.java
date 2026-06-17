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
@DisplayName("RiderController 测试")
class RiderControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private RiderService riderService;
    @MockBean private OrderService orderService;

    private final User testRider = TestBeans.createTestRider();

    @Test @DisplayName("跳转登录页")
    void loginPage() throws Exception {
        mockMvc.perform(get("/rider/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login?role=rider"));
    }

    @Test @DisplayName("骑手登录成功")
    void doLoginSuccess() throws Exception {
        when(riderService.login("rider1", "123")).thenReturn(testRider);
        mockMvc.perform(post("/rider/doLogin")
                        .param("username", "rider1").param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rider/index"));
    }

    @Test @DisplayName("骑手登录失败")
    void doLoginFail() throws Exception {
        when(riderService.login("rider1", "wrong")).thenReturn(null);
        mockMvc.perform(post("/rider/doLogin")
                        .param("username", "rider1").param("password", "wrong"))
                .andExpect(status().is3xxRedirection());
    }

    @Test @DisplayName("骑手首页需登录")
    void indexRequiresLogin() throws Exception {
        mockMvc.perform(get("/rider/index"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rider/login"));
    }

    @Test @DisplayName("已登录骑手首页")
    void indexLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("rider", testRider);
        com.javaBean.PageResult<com.javaBean.Order> emptyResult = new com.javaBean.PageResult<>();
        when(riderService.getPendingOrders(1, 1)).thenReturn(emptyResult);
        when(riderService.getRiderPickupOrders(3, 1, 1)).thenReturn(emptyResult);
        when(riderService.getRiderDeliveringOrders(3, 1, 1)).thenReturn(emptyResult);
        when(riderService.getRiderCompletedOrders(3, 1, 1)).thenReturn(emptyResult);
        when(riderService.getUnreadCount(3)).thenReturn(0);
        when(riderService.getPendingOrders(1, 10)).thenReturn(new com.javaBean.PageResult<>());
        mockMvc.perform(get("/rider/index").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("rider/index"));
    }

    @Test @DisplayName("个人中心")
    void profile() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("rider", testRider);
        when(riderService.getById(3)).thenReturn(testRider);
        when(riderService.getTotalCompletedCount(3)).thenReturn(10);
        when(riderService.getTotalIncome(3)).thenReturn(500.0);
        when(riderService.getUnreadCount(3)).thenReturn(2);
        mockMvc.perform(get("/rider/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("rider/profile"));
    }

    @Test @DisplayName("收入明细")
    void income() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("rider", testRider);
        when(riderService.getById(3)).thenReturn(testRider);
        when(riderService.getTotalIncome(3)).thenReturn(5000.0);
        when(riderService.getTotalCompletedCount(3)).thenReturn(100);
        mockMvc.perform(get("/rider/income").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("rider/income"));
    }

    @Test @DisplayName("退出登录")
    void logout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("rider", testRider);
        mockMvc.perform(get("/rider/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login?role=rider"));
    }
}
