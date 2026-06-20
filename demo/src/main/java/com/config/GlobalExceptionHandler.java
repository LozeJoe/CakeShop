package com.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器 — 统一拦截所有未捕获异常，返回中文友好错误页，
 * 替代 Spring Boot 默认的 WhitleLabel 白页。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 兜底：捕获所有未处理的异常
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception e) {
        log.error("请求异常 — URL: {}, 错误: {}", request.getRequestURL(), e.getMessage(), e);

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "服务器内部错误，请稍后再试");
        mav.addObject("detail", e.getMessage());
        return mav;
    }

    /**
     * 404 — 通常由 NoHandlerFoundException 触发
     * （需要在 application.yml 中开启 spring.mvc.throw-exception-if-no-handler-found=true）
     */
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ModelAndView handle404(HttpServletRequest request, Exception e) {
        log.warn("404 页面不存在 — URL: {}", request.getRequestURL());

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "页面不存在");
        mav.addObject("detail", "您访问的地址 " + request.getRequestURL() + " 未找到");
        return mav;
    }

    /**
     * 运行时异常（含空指针、数组越界等）
     */
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(HttpServletRequest request, RuntimeException e) {
        log.error("运行时异常 — URL: {}, 错误: {}", request.getRequestURL(), e.getMessage(), e);

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "操作失败，请稍后再试");
        mav.addObject("detail", e.getMessage());
        return mav;
    }
}
