package com.distributed_streaming_platform.content_service.dto.response;

import com.distributed_streaming_platform.content_service.enums.ContentStatus;
import com.distributed_streaming_platform.content_service.enums.ContentType;
import com.distributed_streaming_platform.content_service.enums.Genre;
import com.distributed_streaming_platform.content_service.enums.Language;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ContentResponse {

    private Long id;

    private String title;
    private String description;

    private ContentType type;
    private Genre genre;

    private Language language;
    private Integer duration;

    private LocalDate releaseDate;
    private ContentStatus status;

    private String thumbnailUrl;
    private String trailerUrl;
    private LocalDateTime createdAt;
}