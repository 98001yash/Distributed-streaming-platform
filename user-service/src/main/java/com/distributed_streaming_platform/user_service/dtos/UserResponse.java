package com.distributed_streaming_platform.user_service.dtos;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private String profileImage;
    private String bio;
    private String country;
    private String language;
}
