package com.distributed_streaming_platform.user_service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler){

        String email = request.getHeader("X-User-Email");
        String idHeader = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        log.info("Incoming headers → userId={}, email={}, role={}", idHeader, email, role);

        try {
            if (idHeader != null && !idHeader.isBlank() &&
                    email != null && role != null) {

                Long userId = Long.parseLong(idHeader);
                UserContextHolder.setUser(userId, email, role);
            } else {
                log.warn("Missing or invalid headers for authentication");
            }
        } catch (Exception e) {
            log.error("Error parsing userId from header: {}", idHeader, e);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception e){
        UserContextHolder.clear();
    }
}
