package com.distributed_streaming_platform.user_service.service.Impl;

import com.distributed_streaming_platform.user_service.auth.UserContextHolder;
import com.distributed_streaming_platform.user_service.dtos.UpdateUserRequest;
import com.distributed_streaming_platform.user_service.dtos.UserResponse;
import com.distributed_streaming_platform.user_service.entity.User;
import com.distributed_streaming_platform.user_service.exceptions.UnauthorizedAccessException;
import com.distributed_streaming_platform.user_service.exceptions.UserNotFoundException;
import com.distributed_streaming_platform.user_service.repository.UserRepository;
import com.distributed_streaming_platform.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser() {

        Long userId = UserContextHolder.getCurrentUserId();

        if (userId == null) {
            log.warn("Unauthorized access attempt: userId is null");
            throw new UnauthorizedAccessException("User not authenticated");
        }

        return getUserByIdCached(userId);
    }

    @Cacheable(value = "users", key = "#userId")
    public UserResponse getUserByIdCached(Long userId) {

        log.info("Fetching user from DB for userId={} (CACHE MISS)", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for userId={}", userId);
                    return new UserNotFoundException("User not found");
                });

        return mapToResponse(user);
    }
    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponse updateCurrentUser(UpdateUserRequest request) {

        Long userId = UserContextHolder.getCurrentUserId();

        if (userId == null) {
            log.warn("Unauthorized update attempt: userId is null");
            throw new UnauthorizedAccessException("User not authenticated");
        }

        log.info("Updating profile for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for update userId={}", userId);
                    return new UserNotFoundException("User not found");
                });

        user.setName(request.getName());
        user.setProfileImage(request.getProfileImage());
        user.setBio(request.getBio());
        user.setCountry(request.getCountry());
        user.setLanguage(request.getLanguage());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info("User updated successfully and cache evicted for userId={}", userId);

        return mapToResponse(user);

    }



    @Override
    @Transactional
    public UserResponse createUser(Long id, String email) {

        log.info("Creating user in user-service with id={}, email={}", id, email);

        if (userRepository.existsById(id)) {
            log.warn("User already exists with id={}", id);
            return mapToResponse(userRepository.findById(id).get());
        }

        User user = User.builder()
                .id(id)
                .email(email)
                .name("New User")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        log.info("User created successfully with id={}", id);

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .bio(user.getBio())
                .country(user.getCountry())
                .language(user.getLanguage())
                .build();
    }

}
