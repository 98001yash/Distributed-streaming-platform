package com.distributed_streaming_platform.content_service.dto.response;

import com.distributed_streaming_platform.content_service.enums.ContentType;
import com.distributed_streaming_platform.content_service.enums.Genre;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentSummaryResponse {

    private Long id;
    private String title;
    private Genre genre;
    private ContentType type;
    private String thumbnailUrl;
}