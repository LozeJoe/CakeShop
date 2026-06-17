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
 * E2E 骑手全流程测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E2E - 骑手全流程测试")
class RiderFlowE2ETest {

    @LocalServerPort private int port;
    @Autowired private TestRestTemplate restTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() { baseUrl = "http://localhost:" + port; }

    @Test @Order(1) @DisplayName("骑手登录页跳转")
    void riderLoginPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/rider/login", String.class);
        // TestRestTemplate follows redirects — should end at login page (200)
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test @Order(2) @DisplayName("未登录访问骑手首页 - 可访问")
    void riderIndex() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/rider/index", String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection());
    }

    @Test @Order(3) @DisplayName("骑手登录 POST")
    void riderDoLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>("username=rider1&password=123", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/rider/doLogin", request, String.class);
        assertTrue(response.getStatusCode().is3xxRedirection());
        assertTrue(response.getHeaders().getLocation().toString().contains("/rider/index"));
    }

    @Test @Order(4) @DisplayName("骑手登录失败")
    void riderDoLoginFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>("username=rider1&password=wrong", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/rider/doLogin", request, String.class);
        assertTrue(response.getStatusCode().is3xxRedirection());
    }

    @Test @Order(5) @DisplayName("未登录访问收入页")
    void incomePage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/rider/income", String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection());
    }

    @Test @Order(6) @DisplayName("未登录访问个人中心")
    void profilePage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/rider/profile", String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is3xxRedirection());
    }
}
