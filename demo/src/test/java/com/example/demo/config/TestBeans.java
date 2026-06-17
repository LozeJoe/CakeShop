package com.example.demo.config;

import com.javaBean.*;
import org.springframework.boot.test.context.TestConfiguration;

/**
 * Shared test beans and mocks for unit tests.
 */
@TestConfiguration
public class TestBeans {

    public static User createTestUser() {
        User u = new User();
        u.setId(1);
        u.setUsername("testuser");
        u.setPassword("202cb962ac59075b964b07152d234b70"); // 123
        u.setName("测试用户");
        u.setPhone("13800138000");
        u.setAddress("测试地址");
        u.setIsadmin("0");
        u.setIsvalidate("1");
        u.setStatus(0);
        return u;
    }

    public static User createTestAdmin() {
        User u = createTestUser();
        u.setId(2);
        u.setUsername("admin");
        u.setIsadmin("1");
        return u;
    }

    public static User createTestRider() {
        User u = createTestUser();
        u.setId(3);
        u.setUsername("rider1");
        u.setIsadmin("2");
        return u;
    }

    public static Goods createTestGoods(int id) {
        Goods g = new Goods();
        g.setId(id);
        g.setName("测试商品" + id);
        g.setPrice(100.0);
        g.setStock(50);
        g.setSales(10);
        g.setTypeId(1);
        return g;
    }

    public static Order createTestOrder() {
        Order o = new Order();
        o.setId("TEST20260601001");
        o.setUserId(1);
        o.setName("测试用户");
        o.setPhone("13800138000");
        o.setAddress("测试地址");
        o.setTotal(200.0);
        o.setAmount(2);
        o.setStatus(1);
        o.setPaytype(1);
        return o;
    }
}
