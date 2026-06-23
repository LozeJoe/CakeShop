package com.config;

import com.javaBean.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Admin interceptor — returns HTTP 403 Forbidden when a logged-in non-admin
 * user tries to access /admin/* pages.
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/user/login");
            return false;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("/user/login");
            return false;
        }

        if (!"1".equals(user.getIsadmin())) {
            // Logged-in but not admin → 403 Forbidden
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问管理后台");
            return false;
        }

        // 将管理员角色写入 request，供视图和控制器使用
        // 如果 adminRole 为空或未知，默认给 super_admin（兼容旧数据）
        String role = user.getAdminRole();
        if (role == null || role.isEmpty() || !("super_admin".equals(role) || "admin".equals(role))) {
            role = "super_admin";  // 兜底：未知角色默认给最高权限
        }
        request.setAttribute("adminRole", role);
        return true;
    }
}
