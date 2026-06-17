package com.example.demo.unit.service;

import com.example.demo.config.TestBeans;
import com.javaBean.Goods;
import com.mapper.GoodsMapper;
import com.service.GoodsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoodsService 单元测试")
class GoodsServiceUnitTest {

    @Mock private GoodsMapper goodsMapper;
    @InjectMocks private GoodsServiceImpl goodsService;

    @Test @DisplayName("分页查询所有商品")
    void getGoodsByPage() {
        when(goodsMapper.getGoodsCount()).thenReturn(3);
        when(goodsMapper.getGoodsByPage(0, 2)).thenReturn(Arrays.asList(TestBeans.createTestGoods(1), TestBeans.createTestGoods(2)));
        var result = goodsService.getGoodsByPage(1, 2);
        assertEquals(2, result.getData().size());
        assertEquals(3, result.getTotalCount());
    }

    @Test @DisplayName("按类型分页查询")
    void getGoodsByTypePage() {
        when(goodsMapper.getGoodsCountByType(1)).thenReturn(2);
        when(goodsMapper.getGoodsByTypePage(1, 0, 10)).thenReturn(Arrays.asList(TestBeans.createTestGoods(1), TestBeans.createTestGoods(2)));
        var result = goodsService.getGoodsByTypePage(1, 1, 10);
        assertEquals(2, result.getData().size());
    }

    @Test @DisplayName("搜索商品")
    void searchGoodsPage() {
        when(goodsMapper.searchGoodsCount("蛋糕")).thenReturn(1);
        when(goodsMapper.searchGoodsPage("蛋糕", 0, 10)).thenReturn(Arrays.asList(TestBeans.createTestGoods(1)));
        var result = goodsService.searchGoodsPage("蛋糕", 1, 10);
        assertEquals(1, result.getData().size());
    }

    @Test @DisplayName("搜索为空")
    void searchGoodsPageEmpty() {
        when(goodsMapper.searchGoodsCount("xxxxx")).thenReturn(0);
        when(goodsMapper.searchGoodsPage("xxxxx", 0, 10)).thenReturn(Arrays.asList());
        assertTrue(goodsService.searchGoodsPage("xxxxx", 1, 10).getData().isEmpty());
    }

    @Test @DisplayName("获取热销商品分页")
    void getTopSellGoodsPage() {
        when(goodsMapper.getGoodsCount()).thenReturn(2);
        when(goodsMapper.getTopSellGoodsPage(0, 10)).thenReturn(Arrays.asList(TestBeans.createTestGoods(1), TestBeans.createTestGoods(2)));
        var result = goodsService.getTopSellGoodsPage(1, 10);
        assertEquals(2, result.getData().size());
    }

    @Test @DisplayName("获取最新商品分页")
    void getNewGoodsPage() {
        when(goodsMapper.getGoodsCount()).thenReturn(2);
        when(goodsMapper.getNewGoodsPage(0, 10)).thenReturn(Arrays.asList(TestBeans.createTestGoods(1), TestBeans.createTestGoods(2)));
        var result = goodsService.getNewGoodsPage(1, 10);
        assertEquals(2, result.getData().size());
    }

    @Test @DisplayName("减少库存")
    void decreaseStock() {
        goodsService.decreaseStock(1, 5);
        verify(goodsMapper).decreaseStock(1, 5);
    }

    @Test @DisplayName("增加销量")
    void increaseSales() {
        goodsService.increaseSales(1, 5);
        verify(goodsMapper).increaseSales(1, 5);
    }

    @Test @DisplayName("获取低库存计数")
    void getLowStockCount() {
        when(goodsMapper.getLowStockCount(10)).thenReturn(2);
        assertEquals(2, goodsService.getLowStockCount(10));
    }
}
