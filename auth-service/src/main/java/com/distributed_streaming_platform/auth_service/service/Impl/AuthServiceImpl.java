package com.distributed_streaming_platform.auth_service.service.Impl;

import com.distributed_streaming_platform.auth_service.dtos.AuthResponse;
import com.distributed_streaming_platform.auth_service.dtos.LoginRequest;
import com.distributed_streaming_platform.auth_service.dtos.RegisterRequest;
import com.distributed_streaming_platform.auth_service.entity.RefreshToken;
import com.distributed_streaming_platform.auth_service.entity.User;
import com.distributed_streaming_platform.auth_service.enums.Role;
import com.distributed_streaming_platform.auth_service.exceptions.InvalidCredentialsException;
import com.distributed_streaming_platform.auth_service.exceptions.ResourceNotFoundException;
import com.distributed_streaming_platform.auth_service.exceptions.UserAlreadyExistsException;
import com.distributed_streaming_platform.auth_service.repository.RefreshTokenRepository;
import com.distributed_streaming_platform.auth_service.repository.UserRepository;
import com.distributed_streaming_platform.auth_service.security.CustomUserDetailsService;
import com.distributed_streaming_platform.auth_service.security.JwtService;
import com.distributed_streaming_platform.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;


    @Override
    public AuthResponse register(RegisterRequest request) {

        log .info("Register request received for email: {}",request.getEmail());

        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("User already exists with email: {}",request.getEmail());
            throw new UserAlreadyExistsException("User already exists with email");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        log.info("User registered successfully with id: {}",user.getId());

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt for email: {}",request.getEmail());

        try{
         authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(
                         request.getEmail(),
                         request.getPassword()
                 )
         );
        }catch(Exception e){
            log.error("Invalid login attempt for email: {}",request.getEmail());
            throw new InvalidCredentialsException("Invalid Email or password");
        }

        User user=  userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> {
                    log.error("User not found for email: {}",request.getEmail());
                    return new ResourceNotFoundException("User not found");
                });

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = createRefreshToken(user);

        log.info("User logged in successfully: {}",user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(String token) {

        log.info("Refresh token request received");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(()-> {
                    log.error("Refresh Token not found");
                    return new ResourceNotFoundException("Invalid refresh token");
                });

        if (refreshToken.getRevoked() || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token is expired or revoked");
            throw new InvalidCredentialsException("Refresh token expired or revoked");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        log.info("Access token refreshed for user: {}", user.getEmail());
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .build();
    }

    private String createRefreshToken(User user){

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        log.debug("Refresh Token created for user: {}",user.getEmail());
        return refreshToken.getToken();
    }
}
