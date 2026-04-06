package com.distributed_streaming_platform.streaming_service.auth;


import feign.RequestInterceptor;
import feign.RequestTemplate;
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        String email = UserContextHolder.getUserEmail();
        String role = UserContextHolder.getUserRole();

        if (email != null) {
            template.header("X-User-Email", email);
        }

        if (role != null) {
            template.header("X-User-Role", role);
        }
    }
}
