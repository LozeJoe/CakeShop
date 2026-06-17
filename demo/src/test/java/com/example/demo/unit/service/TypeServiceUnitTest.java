package com.example.demo.unit.service;

import com.example.demo.config.TestBeans;
import com.javaBean.Type;
import com.mapper.TypeMapper;
import com.service.TypeServiceImpl;
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
@DisplayName("TypeService 单元测试")
class TypeServiceUnitTest {

    @Mock private TypeMapper typeMapper;
    @InjectMocks private TypeServiceImpl typeService;

    private Type createType(int id, String name, int pid) {
        Type t = new Type();
        t.setId(id);
        t.setName(name);
        t.setPid(pid);
        return t;
    }

    @Nested
    @DisplayName("查询操作")
    class QueryOperations {
        @Test @DisplayName("获取所有分类")
        void getAllTypes() {
            List<Type> types = Arrays.asList(
                createType(1, "蛋糕", 0),
                createType(2, "甜品", 0)
            );
            when(typeMapper.getAllTypes()).thenReturn(types);
            assertEquals(2, typeService.getAllTypes().size());
        }

        @Test @DisplayName("按ID获取分类")
        void getTypeById() {
            when(typeMapper.getTypeById(1)).thenReturn(createType(1, "蛋糕", 0));
            Type result = typeService.getTypeById(1);
            assertEquals("蛋糕", result.getName());
        }

        @Test @DisplayName("获取分类数量")
        void getTypeCount() {
            when(typeMapper.getTypeCount()).thenReturn(10);
            assertEquals(10, typeService.getTypeCount());
        }

        @Test @DisplayName("获取不存在的分类返回null")
        void getTypeByIdNotFound() {
            when(typeMapper.getTypeById(999)).thenReturn(null);
            assertNull(typeService.getTypeById(999));
        }
    }

    @Nested
    @DisplayName("增删改操作")
    class MutateOperations {
        @Test @DisplayName("添加分类")
        void addType() {
            Type t = createType(0, "新分类", 0);
            typeService.addType(t);
            verify(typeMapper).addType(t);
        }

        @Test @DisplayName("更新分类")
        void updateType() {
            Type t = createType(1, "更新后名称", 0);
            typeService.updateType(t);
            verify(typeMapper).updateType(t);
        }

        @Test @DisplayName("删除分类")
        void deleteType() {
            typeService.deleteType(1);
            verify(typeMapper).deleteType(1);
        }
    }
}
