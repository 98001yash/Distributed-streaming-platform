package com.distributed_streaming_platform.auth_service.dtos;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
}
