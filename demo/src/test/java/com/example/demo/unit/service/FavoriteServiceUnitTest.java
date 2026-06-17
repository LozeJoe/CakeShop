package com.example.demo.unit.service;

import com.javaBean.Favorite;
import com.mapper.FavoriteMapper;
import com.service.FavoriteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavoriteService 单元测试")
class FavoriteServiceUnitTest {

    @Mock private FavoriteMapper favoriteMapper;
    @InjectMocks private FavoriteServiceImpl favoriteService;

    private Favorite createFavorite(int id, int userId, int goodsId) {
        Favorite f = new Favorite();
        f.setId(id);
        f.setUserId(userId);
        f.setGoodsId(goodsId);
        return f;
    }

    @Nested
    @DisplayName("查询操作")
    class QueryOperations {
        @Test @DisplayName("获取用户的收藏列表")
        void getFavoritesByUserId() {
            when(favoriteMapper.getFavoritesByUserId(1)).thenReturn(
                Arrays.asList(createFavorite(1, 1, 10), createFavorite(2, 1, 20))
            );
            assertEquals(2, favoriteService.getFavoritesByUserId(1).size());
        }

        @Test @DisplayName("获取收藏数量")
        void getFavoriteCount() {
            when(favoriteMapper.getFavoriteCount(1)).thenReturn(3);
            assertEquals(3, favoriteService.getFavoriteCount(1));
        }

        @Test @DisplayName("空收藏列表返回空")
        void emptyFavorites() {
            when(favoriteMapper.getFavoritesByUserId(999)).thenReturn(Arrays.asList());
            assertTrue(favoriteService.getFavoritesByUserId(999).isEmpty());
        }

        @Test @DisplayName("判断商品是否已收藏")
        void isFavorited() {
            when(favoriteMapper.getFavorite(1, 10)).thenReturn(createFavorite(1, 1, 10));
            assertTrue(favoriteService.isFavorited(1, 10));
        }

        @Test @DisplayName("判断商品未收藏")
        void isNotFavorited() {
            when(favoriteMapper.getFavorite(1, 99)).thenReturn(null);
            assertFalse(favoriteService.isFavorited(1, 99));
        }
    }

    @Nested
    @DisplayName("增删操作")
    class MutateOperations {
        @Test @DisplayName("添加收藏")
        void addFavorite() {
            Favorite f = createFavorite(0, 1, 10);
            favoriteService.addFavorite(f);
            verify(favoriteMapper).addFavorite(f);
        }

        @Test @DisplayName("按ID删除收藏")
        void deleteFavorite() {
            favoriteService.deleteFavorite(1);
            verify(favoriteMapper).deleteFavorite(1);
        }

        @Test @DisplayName("按用户和商品删除收藏")
        void removeFavorite() {
            favoriteService.removeFavorite(1, 10);
            verify(favoriteMapper).removeFavorite(1, 10);
        }
    }
}
