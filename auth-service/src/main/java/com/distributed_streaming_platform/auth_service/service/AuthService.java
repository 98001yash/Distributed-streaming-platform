package com.distributed_streaming_platform.auth_service.service;

import com.distributed_streaming_platform.auth_service.dtos.AuthResponse;
import com.distributed_streaming_platform.auth_service.dtos.LoginRequest;
import com.distributed_streaming_platform.auth_service.dtos.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);
}
