package com.distributed_streaming_platform.user_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {

    private Long id;

    @Email
    @NotBlank
    private String email;
    private String name;
}
