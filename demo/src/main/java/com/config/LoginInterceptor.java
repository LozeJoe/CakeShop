package com.config;

import com.javaBean.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 * 用于拦截未登录用户访问需要登录的页面
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 拦截请求进行前置处理。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Session中的用户信息
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 如果用户未登录，重定向到登录页面
        if (user == null) {
            response.sendRedirect("/user/login");
            return false;
        }
        
        // 用户已登录，允许访问
        return true;
    }
}