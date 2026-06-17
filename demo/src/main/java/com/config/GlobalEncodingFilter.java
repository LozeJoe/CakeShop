package com.config;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@WebFilter(filterName = "GlobalEncodingFilter", urlPatterns = "/*")
public class GlobalEncodingFilter implements Filter {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        // ——— Encoding ———
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setHeader("Content-Type", "text/html;charset=UTF-8");

        // ——— Security Response Headers ———
        // Prevent clickjacking by disallowing iframe embedding
        resp.setHeader("X-Frame-Options", "SAMEORIGIN");
        // Content Security Policy: restrict script/style sources
        resp.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data:; " +
            "font-src 'self'; " +
            "frame-ancestors 'self'");
        // Prevent MIME-type sniffing
        resp.setHeader("X-Content-Type-Options", "nosniff");
        // Enable browser XSS filter
        resp.setHeader("X-XSS-Protection", "1; mode=block");
        // Strict Transport Security (max-age 1 year)
        resp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        // Referrer policy
        resp.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // ——— CSRF Token ———
        // Generate a CSRF token per session if one doesn't exist yet
        HttpSession session = req.getSession(false);
        if (session != null) {
            String csrfToken = (String) session.getAttribute("_csrf");
            if (csrfToken == null) {
                byte[] tokenBytes = new byte[32];
                RANDOM.nextBytes(tokenBytes);
                csrfToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
                session.setAttribute("_csrf", csrfToken);
            }
            // Make the CSRF token available as a request attribute so templates can render it
            req.setAttribute("_csrf", csrfToken);
        }

        chain.doFilter(request, response);
    }
}
