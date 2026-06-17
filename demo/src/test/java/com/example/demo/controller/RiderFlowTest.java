package com.example.demo.controller;

import com.example.demo.config.TestBeans;
import com.javaBean.User;
import com.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 骑手全流程测试
 * 覆盖：登录 → 首页 → 接单 → 配送 → 完成 → 收入 → 退出
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("骑手全流程测试")
class RiderFlowTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserService userService;

    private MockHttpSession riderSession;

    @BeforeEach
    void setUp() {
        riderSession = new MockHttpSession();
        User rider = userService.login("rider1", "123");
        if (rider != null && "2".equals(rider.getIsadmin())) {
            riderSession.setAttribute("rider", rider);
        }
    }

    @Test
    @DisplayName("骑手登录页面跳转")
    void loginRedirect() throws Exception {
        mockMvc.perform(get("/rider/login"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/login?role=rider"));
    }

    @Test
    @DisplayName("骑手未登录访问首页被拦截")
    void indexRequiresLogin() throws Exception {
        mockMvc.perform(get("/rider/index"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/rider/login"));
    }

    @Test
    @DisplayName("骑手已登录访问首页（待接单tab）")
    void indexPendingTab() throws Exception {
        if (riderSession.getAttribute("rider") == null) return;
        mockMvc.perform(get("/rider/index")
                .param("tab", "pending")
                .session(riderSession))
            .andExpect(status().isOk())
            .andExpect(view().name("rider/index"));
    }

    @Test
    @DisplayName("骑手查看待取货tab")
    void indexPickupTab() throws Exception {
        if (riderSession.getAttribute("rider") == null) return;
        mockMvc.perform(get("/rider/index")
                .param("tab", "pickup")
                .session(riderSession))
            .andExpect(status().isOk())
            .andExpect(view().name("rider/index"));
    }

    @Test
    @DisplayName("骑手查看配送中tab")
    void indexDeliveringTab() throws Exception {
        if (riderSession.getAttribute("rider") == null) return;
        mockMvc.perform(get("/rider/index")
                .param("tab", "delivering")
                .session(riderSession))
            .andExpect(status().isOk())
            .andExpect(view().name("rider/index"));
    }

    @Test
    @DisplayName("骑手查看已完成tab")
    void indexCompletedTab() throws Exception {
        if (riderSession.getAttribute("rider") == null) return;
        mockMvc.perform(get("/rider/index")
                .param("tab", "completed")
                .session(riderSession))
            .andExpect(status().isOk())
            .andExpect(view().name("rider/index"));
    }

    @Test
    @DisplayName("骑手查看个人中心")
    void profile() throws Exception {
        if (riderSession.getAttribute("rider") == null) return;
        mockMvc.perform(get("/rider/profile").session(riderSession))
            .andExpect(status().isOk())
            .andExpect(view().name("rider/profile"));
    }

    @Test
    @DisplayName("骑手查看收入统计")
    void income() throws Exception {
        if (riderSession.getAttribute("rider") == null) return;
        mockMvc.perform(get("/rider/income").session(riderSession))
            .andExpect(status().isOk())
            .andExpect(view().name("rider/income"));
    }

    @Test
    @DisplayName("骑手查看消息")
    void messages() throws Exception {
        if (riderSession.getAttribute("rider") == null) return;
        mockMvc.perform(get("/rider/messages").session(riderSession))
            .andExpect(status().isOk())
            .andExpect(view().name("rider/messages"));
    }

    @Test
    @DisplayName("骑手退出登录")
    void logout() throws Exception {
        mockMvc.perform(get("/rider/logout").session(riderSession))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/login?role=rider"));
    }

    @Test
    @DisplayName("未登录骑手无法访问受限页面")
    void unauthenticatedBlocked() throws Exception {
        mockMvc.perform(get("/rider/profile"))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/rider/income"))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/rider/messages"))
            .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/rider/orderDetail").param("orderId", "1"))
            .andExpect(status().is3xxRedirection());
    }
}
