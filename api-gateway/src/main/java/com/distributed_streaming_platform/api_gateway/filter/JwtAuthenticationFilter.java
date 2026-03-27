package com.distributed_streaming_platform.api_gateway.filter;


import com.distributed_streaming_platform.api_gateway.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {


        String path = exchange.getRequest().getURI().getPath();

        //allow auth endpoints

        if(path.contains("/auth")){
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest() .
                getHeaders() .
                getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing Authorization Header");
        }

        String token = authHeader.substring(7);
        try {

            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            //  Forward user info to downstream services
            exchange.getRequest().mutate()
                    .header("X-User-Email", username)
                    .header("X-User-Role", role)
                    .build();

        } catch (Exception e) {
            return onError(exchange, "Invalid JWT Token");
        }

        return chain.filter(exchange);
        }

    private Mono<Void> onError(ServerWebExchange exchange, String error) {
        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
