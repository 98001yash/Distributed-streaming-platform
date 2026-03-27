package com.distributed_streaming_platform.user_service.service;

import com.distributed_streaming_platform.user_service.dtos.UpdateUserRequest;
import com.distributed_streaming_platform.user_service.dtos.UserResponse;

public interface UserService {

    UserResponse getCurrentUser();

    UserResponse updateCurrentUser(UpdateUserRequest request);

    UserResponse createUser(Long id, String email);
}
