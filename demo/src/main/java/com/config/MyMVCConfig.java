package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Spring MVC 自定义配置类
 */
@Configuration
public class MyMVCConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        resolver.setCookieName("lang");
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    /**
     * 登录页、注册页、首页、帮助页等纯展示、无数据查询的页面；
     * 只需要跳转，不需要后端处理逻辑的请求
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/goods/goodList");
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/index.html").setViewName("redirect:/goods/goodList");
        registry.addViewController("/register.html").setViewName("register");
        // /order/orderAdd → redirect to cart where the checkout modal lives
        registry.addViewController("/order/orderAdd").setViewName("redirect:/cart/cartList");
        // /user/logout → alias, handled by UserController but also available as a view redirect
        registry.addViewController("/user/logout").setViewName("redirect:/user/loginout");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多语言切换拦截器
        registry.addInterceptor(localeChangeInterceptor());

        // 注册登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                // 需要拦截的路径
                .addPathPatterns("/user/**", "/order/**", "/cart/**", "/admin/**", "/review/**")
                // 不需要拦截的路径
                .excludePathPatterns("/user/login", "/user/register", "/user/logout", "/user/loginout", "/css/**", "/js/**", "/images/**", "/fonts/**", "/login.html", "/register.html", "/error");

        // 注册管理员拦截器 — 返回 403 而非重定向到登录页
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**");
    }
}