package org.ychan.lablab.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.ychan.lablab.eception.BusinessException;
import org.ychan.lablab.entity.admin.Admin;
import org.ychan.lablab.enums.RoleEnum;
import org.ychan.lablab.service.AdminService;

import lombok.RequiredArgsConstructor;
import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AdminService adminService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        String token = parseToken(authorization);
        if (token == null || token.isBlank()) {
            throw new BusinessException("请先登录");
        }

        if (!adminService.validateToken(token)) {
            throw new BusinessException("登录已过期，请重新登录");
        }

        // 检查角色权限
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Class<?> clazz = handlerMethod.getBeanType();

            // 检查类上的注解
            RequiredRole classAnnotation = clazz.getAnnotation(RequiredRole.class);
            // 检查方法上的注解
            RequiredRole methodAnnotation = method.getAnnotation(RequiredRole.class);

            if (classAnnotation != null || methodAnnotation != null) {
                Admin admin = adminService.getAdminByToken(token);
                if (admin == null) {
                    throw new BusinessException("登录已过期，请重新登录");
                }

                boolean hasPermission = false;
                if (methodAnnotation != null) {
                    hasPermission = checkRole(admin.getRole(), methodAnnotation.value());
                } else if (classAnnotation != null) {
                    hasPermission = checkRole(admin.getRole(), classAnnotation.value());
                }

                if (!hasPermission) {
                    throw new BusinessException("权限不足");
                }
            }
        }

        return true;
    }

    private boolean checkRole(String userRole, RoleEnum[] requiredRoles) {
        for (RoleEnum role : requiredRoles) {
            if (role.getCode().equals(userRole)) {
                return true;
            }
        }
        return false;
    }

    private static String parseToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        String s = authorization.trim();
        if (s.startsWith("Bearer ")) {
            return s.substring(7).trim();
        }
        return s;
    }
}