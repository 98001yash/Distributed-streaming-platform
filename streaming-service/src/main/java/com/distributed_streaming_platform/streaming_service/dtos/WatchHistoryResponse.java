package com.distributed_streaming_platform.streaming_service.dtos;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WatchHistoryResponse {

    private Long contentId;
    private Long watchTime;
    private String status;

    private LocalDateTime startedAt;
    private LocalDateTime lastUpdatedAt;
}
