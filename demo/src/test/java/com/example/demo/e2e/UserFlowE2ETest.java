package com.example.demo.e2e;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E 用户全流程测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E2E - 用户全流程测试")
class UserFlowE2ETest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() { baseUrl = "http://localhost:" + port; }

    @Test @Order(1) @DisplayName("访问首页 - 可正常返回")
    void homePage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/", String.class);
        // TestRestTemplate follows redirects by default, so we get 200
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test @Order(2) @DisplayName("访问登录页")
    void loginPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/user/login", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test @Order(3) @DisplayName("访问商品列表")
    void goodsList() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/goods/goodList", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test @Order(4) @DisplayName("搜索商品")
    void searchGoods() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/goods/search?keyword=蛋糕&page=1", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test @Order(5) @DisplayName("商品详情页")
    void goodsDetail() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/goods/detail?id=1", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test @Order(6) @DisplayName("热销排行页")
    void topSellPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/goods/topSell", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test @Order(7) @DisplayName("访问注册页")
    void registerPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/user/register", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test @Order(8) @DisplayName("未登录访问购物车 - 可访问(拦截器重定向后被跟随)")
    void cartAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/cart/cartList", String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection());
    }

    @Test @Order(9) @DisplayName("未登录访问订单")
    void orderAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/order/myOrder", String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection());
    }
}
