package com.config;

import com.javaBean.User;
import com.service.AdminLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Aspect
@Component
public class AdminLogAspect {

    @Resource
    private AdminLogService adminLogService;

    @Before("execution(* com.controller.AdminController.*(..)) && !execution(* com.controller.AdminController.index(..)) && !execution(* com.controller.AdminController.orders(..)) && !execution(* com.controller.AdminController.goods*(..)) && !execution(* com.controller.AdminController.types*(..)) && !execution(* com.controller.AdminController.users*(..)) && !execution(* com.controller.AdminController.settings*(..)) && !execution(* com.controller.AdminController.systemConfig*(..)) && !execution(* com.controller.AdminController.saveSystemConfig*(..)) && !execution(* com.controller.AdminController.orderDetail*(..))")
    public void logAdminAction(JoinPoint joinPoint) {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return;
            HttpServletRequest request = attrs.getRequest();
            HttpSession session = request.getSession(false);
            if (session == null) return;
            User admin = (User) session.getAttribute("user");
            if (admin == null || !"1".equals(admin.getIsadmin())) return;

            String method = joinPoint.getSignature().getName();
            String target = "";
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] != null && !(args[0] instanceof HttpServletRequest)) {
                target = args[0].toString();
            }
            String ip = request.getRemoteAddr();
            adminLogService.log(admin.getUsername(), method, target, ip);
        } catch (Exception ignored) {}
    }
}
