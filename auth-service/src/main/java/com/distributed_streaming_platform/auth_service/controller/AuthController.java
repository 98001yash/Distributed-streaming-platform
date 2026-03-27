package com.distributed_streaming_platform.auth_service.controller;


import com.distributed_streaming_platform.auth_service.dtos.AuthResponse;
import com.distributed_streaming_platform.auth_service.dtos.LoginRequest;
import com.distributed_streaming_platform.auth_service.dtos.RefreshTokenRequest;
import com.distributed_streaming_platform.auth_service.dtos.RegisterRequest;
import com.distributed_streaming_platform.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody
                                                     RegisterRequest request){

        log.info("Received rehister request for emal: {}",request.getEmail());
        AuthResponse response = authService.register(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){

        log.info("Received login request for emal: {}",request.getEmail());
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request){
        log.info("Received refresh token request");

        AuthResponse response= authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
