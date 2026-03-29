package com.distributed_streaming_platform.api_gateway.filter;

import com.distributed_streaming_platform.api_gateway.utils.JwtUtil;
import io.jsonwebtoken.JwtException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            //  Skip public endpoints
            if (path.startsWith("/auth") || path.startsWith("/actuator")) {
                log.info("Skipping authentication for path: {}", path);
                return chain.filter(exchange);
            }

            log.info("Authenticating request to: {}", path);

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Missing Authorization Header");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7).trim();

            try {
                String username = jwtUtil.extractUsername(token);
                Long userId = jwtUtil.extractUserId(token);
                String role = jwtUtil.extractRole(token);

                if (userId == null) {
                    log.error("JWT does not contain userId");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                // 🔥 Proper mutation (IMPORTANT)
                ServerWebExchange modifiedExchange = exchange
                        .mutate()
                        .request(builder -> builder
                                .header("X-User-Id", String.valueOf(userId))
                                .header("X-User-Email", username)
                                .header("X-User-Role", role)
                        )
                        .build();

                log.info("Authenticated → userId={}, email={}, role={}", userId, username, role);

                return chain.filter(modifiedExchange);

            } catch (JwtException e) {
                log.error("Invalid JWT: {}", e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
    }


}
