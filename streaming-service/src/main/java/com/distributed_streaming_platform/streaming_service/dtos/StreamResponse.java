package com.distributed_streaming_platform.streaming_service.dtos;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StreamResponse {

    private Long contentId;
    private String streamUrl;
}