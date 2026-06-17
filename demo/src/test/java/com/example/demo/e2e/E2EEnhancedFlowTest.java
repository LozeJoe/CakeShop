package com.example.demo.e2e;

import com.javaBean.User;
import com.service.UserService;
import com.service.RiderService;
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
 * 增强版 E2E 全业务流程测试
 *
 * 覆盖用户、管理员、骑手三端核心页面访问路径
 * + 权限控制验证
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("E2E 增强全业务流程测试")
class E2EEnhancedFlowTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserService userService;
    @Autowired private RiderService riderService;

    private MockHttpSession userSession;
    private MockHttpSession adminSession;
    private MockHttpSession riderSession;
    private boolean hasUser, hasAdmin, hasRider;

    @BeforeEach
    void setUp() {
        userSession = new MockHttpSession();
        User user = userService.login("testuser", "123");
        hasUser = user != null;
        if (hasUser) userSession.setAttribute("user", user);

        adminSession = new MockHttpSession();
        User admin = userService.login("admin", "123");
        hasAdmin = admin != null && "1".equals(admin.getIsadmin());
        if (hasAdmin) adminSession.setAttribute("user", admin);

        riderSession = new MockHttpSession();
        User rider = riderService.login("rider1", "123");
        hasRider = rider != null;
        if (hasRider) riderSession.setAttribute("rider", rider);
    }

    // ======================================================
    // 公共页面（无需登录）
    // ======================================================

    @Test @DisplayName("首页加载")
    void homePage() throws Exception {
        mockMvc.perform(get("/goods/goodList"))
            .andExpect(status().isOk())
            .andExpect(view().name("goodsList"));
    }

    @Test @DisplayName("搜索商品")
    void searchGoods() throws Exception {
        mockMvc.perform(get("/goods/search").param("keyword", "蛋糕"))
            .andExpect(status().isOk())
            .andExpect(view().name("goodsList"))
            .andExpect(model().attributeExists("keyword"));
    }

    @Test @DisplayName("查看商品详情")
    void goodsDetail() throws Exception {
        mockMvc.perform(get("/goods/detail").param("id", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("goodsDetail"));
    }

    @Test @DisplayName("热销排行")
    void topSell() throws Exception {
        mockMvc.perform(get("/goods/topSell"))
            .andExpect(status().isOk())
            .andExpect(view().name("topSell"));
    }

    @Test @DisplayName("新品上市")
    void newGoods() throws Exception {
        mockMvc.perform(get("/goods/newGoods"))
            .andExpect(status().isOk())
            .andExpect(view().name("newGoods"));
    }

    // ======================================================
    // 用户端（需登录）
    // ======================================================

    @Test @DisplayName("【用户】加入购物车后跳转")
    void userAddToCart() throws Exception {
        if (!hasUser) return;
        mockMvc.perform(post("/cart/addToCart")
                .param("goodId", "1").param("amount", "1").param("from", "list")
                .session(userSession))
            .andExpect(status().is3xxRedirection());
    }

    @Test @DisplayName("【用户】查看购物车")
    void userViewCart() throws Exception {
        if (!hasUser) return;
        mockMvc.perform(get("/cart/cartList").session(userSession))
            .andExpect(status().isOk()).andExpect(view().name("cartList"));
    }

    @Test @DisplayName("【用户】我的订单")
    void userMyOrders() throws Exception {
        if (!hasUser) return;
        mockMvc.perform(get("/order/myOrder").session(userSession))
            .andExpect(status().isOk()).andExpect(view().name("myOrder"));
    }

    // ======================================================
    // 骑手端
    // ======================================================

    @Test @DisplayName("【骑手】首页待接单")
    void riderPending() throws Exception {
        if (!hasRider) return;
        mockMvc.perform(get("/rider/index").param("tab", "pending").session(riderSession))
            .andExpect(status().isOk()).andExpect(view().name("rider/index"));
    }

    @Test @DisplayName("【骑手】已完成")
    void riderCompleted() throws Exception {
        if (!hasRider) return;
        mockMvc.perform(get("/rider/index").param("tab", "completed").session(riderSession))
            .andExpect(status().isOk()).andExpect(view().name("rider/index"));
    }

    @Test @DisplayName("【骑手】收入统计")
    void riderIncome() throws Exception {
        if (!hasRider) return;
        mockMvc.perform(get("/rider/income").session(riderSession))
            .andExpect(status().isOk()).andExpect(view().name("rider/income"));
    }

    @Test @DisplayName("【骑手】个人信息")
    void riderProfile() throws Exception {
        if (!hasRider) return;
        mockMvc.perform(get("/rider/profile").session(riderSession))
            .andExpect(status().isOk()).andExpect(view().name("rider/profile"));
    }

    // ======================================================
    // 管理端
    // ======================================================

    @Test @DisplayName("【管理】后台首页")
    void adminDashboard() throws Exception {
        if (!hasAdmin) return;
        mockMvc.perform(get("/admin/index").session(adminSession))
            .andExpect(status().isOk()).andExpect(view().name("admin/index"));
    }

    @Test @DisplayName("【管理】订单管理")
    void adminOrders() throws Exception {
        if (!hasAdmin) return;
        mockMvc.perform(get("/admin/orders").session(adminSession))
            .andExpect(status().isOk()).andExpect(view().name("admin/orderList"));
    }

    @Test @DisplayName("【管理】商品管理")
    void adminGoods() throws Exception {
        if (!hasAdmin) return;
        mockMvc.perform(get("/admin/goods").session(adminSession))
            .andExpect(status().isOk()).andExpect(view().name("admin/goodsList"));
    }

    @Test @DisplayName("【管理】用户管理")
    void adminUsers() throws Exception {
        if (!hasAdmin) return;
        mockMvc.perform(get("/admin/users").session(adminSession))
            .andExpect(status().isOk()).andExpect(view().name("admin/userList"));
    }

    @Test @DisplayName("【管理】分类管理")
    void adminTypes() throws Exception {
        if (!hasAdmin) return;
        mockMvc.perform(get("/admin/types").session(adminSession))
            .andExpect(status().isOk()).andExpect(view().name("admin/typeList"));
    }

    // ======================================================
    // 权限控制验证
    // ======================================================

    @Test @DisplayName("【权限】未登录购物车 → 登录页")
    void unauthenticatedCart() throws Exception {
        mockMvc.perform(get("/cart/cartList"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/login"));
    }

    @Test @DisplayName("【权限】未登录订单 → 登录页")
    void unauthenticatedOrder() throws Exception {
        mockMvc.perform(get("/order/myOrder"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/login"));
    }

    @Test @DisplayName("【权限】非管理员访问后台 → 登录页")
    void nonAdminBlocked() throws Exception {
        if (!hasUser) return;
        mockMvc.perform(get("/admin/orders").session(userSession))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/user/login"));
    }

    @Test @DisplayName("【权限】未登录骑手首页 → 登录页")
    void unauthenticatedRider() throws Exception {
        mockMvc.perform(get("/rider/index"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/rider/login"));
    }
}
