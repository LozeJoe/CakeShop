package com.example.demo.integration.mapper;

import com.javaBean.Goods;
import com.mapper.GoodsMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("GoodsMapper 集成测试")
class GoodsMapperIntegrationTest {

    @Autowired private GoodsMapper goodsMapper;

    @Test @DisplayName("查询所有商品")
    void getAllGoods() {
        List<Goods> goods = goodsMapper.getAllGoods();
        assertNotNull(goods);
        assertTrue(goods.size() >= 8);
    }

    @Test @DisplayName("按ID查询商品")
    void getGoodsById() {
        Goods goods = goodsMapper.getGoodsById(1);
        assertNotNull(goods);
        assertEquals(1, goods.getId());
    }

    @Test @DisplayName("分页查询")
    void getGoodsByPage() {
        List<Goods> goods = goodsMapper.getGoodsByPage(0, 5);
        assertNotNull(goods);
        assertTrue(goods.size() <= 5);
    }

    @Test @DisplayName("按类型分页查询")
    void getGoodsByTypePage() {
        List<Goods> goods = goodsMapper.getGoodsByTypePage(1, 0, 10);
        assertNotNull(goods);
    }

    @Test @DisplayName("搜索商品")
    void searchGoodsPage() {
        List<Goods> goods = goodsMapper.searchGoodsPage("蛋糕", 0, 10);
        assertNotNull(goods);
        assertTrue(goods.size() > 0);
    }

    @Test @DisplayName("热销商品")
    void getTopSellGoodsPage() {
        List<Goods> goods = goodsMapper.getTopSellGoodsPage(0, 10);
        assertNotNull(goods);
    }

    @Test @DisplayName("商品总数")
    void getGoodsCount() {
        int count = goodsMapper.getGoodsCount();
        assertTrue(count > 0);
    }

    @Test @DisplayName("搜索计数")
    void searchGoodsCount() {
        int count = goodsMapper.searchGoodsCount("蛋糕");
        assertTrue(count > 0);
    }
}
