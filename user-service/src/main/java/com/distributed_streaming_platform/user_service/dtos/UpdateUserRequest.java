package com.distributed_streaming_platform.user_service.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String profileImage;
    private String bio;
    private String country;
    private String language;
}
