package com.config;

import com.javaBean.Type;
import com.javaBean.User;
import com.service.CartService;
import com.service.TypeService;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 全局控制器增强 — 为所有页面注入公共变量（购物车数量等），
 * 避免每个 Controller 方法重复设置。
 */
@ControllerAdvice
public class GlobalModelAdvice {

    @Resource
    private CartService cartService;
    
    @Resource
    private TypeService typeService;

    @Resource
    private SystemConfigService systemConfigService;

    @ModelAttribute("count")
    public Integer addCartCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                return cartService.getCartCount(user.getUsername());
            }
        }
        return 0;
    }

    @ModelAttribute("siteName")
    public String siteName() {
        return systemConfigService.get("site_name");
    }

    @ModelAttribute("heroTitle")
    public String heroTitle() {
        return systemConfigService.get("hero_title");
    }

    @ModelAttribute("heroSubtitle")
    public String heroSubtitle() {
        return systemConfigService.get("hero_subtitle");
    }

    @ModelAttribute("pageSize")
    public int pageSize() {
        return systemConfigService.getInt("page_size", 8);
    }

    @ModelAttribute("copyright")
    public String copyright() {
        return systemConfigService.get("copyright");
    }

    @ModelAttribute("typelist")
    public List<Type> typelist() {
        return typeService.getAllTypes();
    }
}
