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
 * E2E 订单全流程测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E2E - 订单全流程测试")
class OrderFlowE2ETest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() { baseUrl = "http://localhost:" + port; }

    @Test @Order(1) @DisplayName("用户登录 - POST")
    void userLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>("userName=vili&userPassword=123", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/user/login", request, String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection());
    }

    @Test @Order(2) @DisplayName("管理员登录 - POST")
    void adminLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>("userName=admin&userPassword=admin", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/user/login", request, String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection());
    }

    @Test @Order(3) @DisplayName("访问错误页面")
    void errorPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/non-existent", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test @Order(4) @DisplayName("管理员后台访问")
    void adminDashboard() {
        // Login with cookie tracking via TestRestTemplate
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> loginReq = new HttpEntity<>("userName=admin&userPassword=admin", headers);
        restTemplate.postForEntity(baseUrl + "/user/login", loginReq, String.class);

        // TestRestTemplate tracks cookies automatically
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/admin/index", String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}
