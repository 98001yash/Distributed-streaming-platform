package com.distributed_streaming_platform.content_ingestion_service.advices;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {


    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    private String path;


    // useful for debugging uploads
    private String traceId;
}
