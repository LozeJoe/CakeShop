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
@DisplayName("UserController 测试")
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;
    @MockBean private GoodsService goodsService;
    @MockBean private TypeService typeService;

    private final User testUser = TestBeans.createTestUser();

    @Test @DisplayName("GET /user/login")
    void loginPage() throws Exception {
        mockMvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test @DisplayName("已登录用户跳转首页")
    void loginPageAlreadyLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        mockMvc.perform(get("/user/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goods/goodList"));
    }

    @Test @DisplayName("POST /user/login - 成功跳转商品列表")
    void loginSuccess() throws Exception {
        when(userService.login("testuser", "123")).thenReturn(testUser);
        mockMvc.perform(post("/user/login")
                        .param("userName", "testuser").param("userPassword", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goods/goodList"));
    }

    @Test @DisplayName("POST /user/login - 失败")
    void loginFail() throws Exception {
        when(userService.login("testuser", "wrong")).thenReturn(null);
        mockMvc.perform(post("/user/login")
                        .param("userName", "testuser").param("userPassword", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test @DisplayName("GET /user/register")
    void registerPage() throws Exception {
        mockMvc.perform(get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test @DisplayName("POST /user/register - 成功")
    void registerSuccess() throws Exception {
        when(userService.getUserByName("newuser")).thenReturn(null);
        mockMvc.perform(post("/user/register")
                        .param("userName", "newuser").param("userPassword", "123456")
                        .param("confirmPassword", "123456"))
                .andExpect(status().is3xxRedirection());
    }

    @Test @DisplayName("POST /user/register - 密码不一致")
    void registerPasswordMismatch() throws Exception {
        mockMvc.perform(post("/user/register")
                        .param("userName", "newuser").param("userPassword", "123456")
                        .param("confirmPassword", "654321"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));
    }

    @Test @DisplayName("GET /user/loginout - 退出")
    void loginout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        mockMvc.perform(get("/user/loginout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }
}
