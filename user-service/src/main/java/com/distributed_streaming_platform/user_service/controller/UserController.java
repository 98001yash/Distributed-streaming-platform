package com.distributed_streaming_platform.user_service.controller;


import com.distributed_streaming_platform.user_service.auth.RoleAllowed;
import com.distributed_streaming_platform.user_service.dtos.UpdateUserRequest;
import com.distributed_streaming_platform.user_service.dtos.UserResponse;
import com.distributed_streaming_platform.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @RoleAllowed({"USER", "ADMIN", "CREATOR"})
    public ResponseEntity<UserResponse> getCurrentUser(){
        log.info("Request received: GET /user/me");
        UserResponse response = userService.getCurrentUser();
        return ResponseEntity.ok(response);
    }


    @PutMapping("/me")
    @RoleAllowed({"USER", "ADMIN", "CREATOR"})
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Request received: PUT /api/user/me");
        UserResponse response = userService.updateCurrentUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/internal")
    public ResponseEntity<UserResponse> createUser(
            @RequestParam Long id, @RequestParam String email) {

        log.info("Internal request to create user id={} email={}", id, email);
        UserResponse response = userService.createUser(id, email);
        return ResponseEntity.ok(response); }
}
