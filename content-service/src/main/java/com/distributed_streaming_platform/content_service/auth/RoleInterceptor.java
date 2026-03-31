package com.distributed_streaming_platform.content_service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

public class RoleInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }


        RoleAllowed roleAllowed = method.getMethodAnnotation(RoleAllowed.class);
        if (roleAllowed == null) { return true;
        }


        String userRole = UserContextHolder.getUserRole();

        if (userRole == null || Arrays.stream(roleAllowed.value())
                .noneMatch(role -> role.equalsIgnoreCase(userRole)))
        {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied");
            return false;
        }
        return true;
    }
}
