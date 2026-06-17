package com.example.demo.controller;

import com.example.demo.config.TestBeans;
import com.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@DisplayName("GoodsController 测试")
class GoodsControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private GoodsService goodsService;
    @MockBean private TypeService typeService;
    @MockBean private FavoriteService favoriteService;
    @MockBean private CartService cartService;

    @Test @DisplayName("GET /goods/goodList")
    void goodList() throws Exception {
        when(goodsService.getGoodsByPage(1, 8)).thenReturn(new com.javaBean.PageResult<>());
        when(typeService.getAllTypes()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/goods/goodList"))
                .andExpect(status().isOk())
                .andExpect(view().name("goodsList"));
    }

    @Test @DisplayName("GET /goods/detail")
    void detail() throws Exception {
        when(goodsService.getGoodsById(1)).thenReturn(TestBeans.createTestGoods(1));
        mockMvc.perform(get("/goods/detail?id=1"))
                .andExpect(status().isOk())
                .andExpect(view().name("goodsDetail"));
    }

    @Test @DisplayName("GET /goods/search")
    void search() throws Exception {
        when(goodsService.searchGoodsPage("蛋糕", 1, 8)).thenReturn(new com.javaBean.PageResult<>());
        when(typeService.getAllTypes()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/goods/search").param("keyword", "蛋糕"))
                .andExpect(status().isOk())
                .andExpect(view().name("goodsList"));
    }

    @Test @DisplayName("GET /goods/topSell")
    void topSell() throws Exception {
        when(goodsService.getTopSellGoodsPage(1, 8)).thenReturn(new com.javaBean.PageResult<>());
        mockMvc.perform(get("/goods/topSell"))
                .andExpect(status().isOk())
                .andExpect(view().name("topSell"));
    }
}
