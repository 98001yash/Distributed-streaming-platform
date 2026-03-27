package com.distributed_streaming_platform.user_service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler){

        String email = request.getHeader("X-User-Email");
        String idHeader = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        if(idHeader != null && email !=null && role !=null){
            Long userId = Long.parseLong(idHeader);
            UserContextHolder.setUser(userId,email, role);
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
