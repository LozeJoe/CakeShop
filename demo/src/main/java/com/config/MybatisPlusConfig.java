package com.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置 — 显式注册分页插件。
 * 注册后即可使用 {@code Page<T>} API 替代手写 LIMIT/OFFSET。
 * 不影响现有手写分页 SQL，两者可并存。
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    /**
     * 执行对应业务操作。
     */
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // MySQL 分页拦截器：自动将 Page<T> 参数翻译为 LIMIT 子句
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页最大 100 条，防止恶意全表查询
        pagination.setMaxLimit(100L);
        // 溢出归首页（page 超过总页数时自动回到第 1 页）
        pagination.setOverflow(true);

        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
