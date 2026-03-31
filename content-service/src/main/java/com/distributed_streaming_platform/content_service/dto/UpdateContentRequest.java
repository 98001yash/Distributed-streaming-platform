package com.distributed_streaming_platform.content_service.dto;

import com.distributed_streaming_platform.content_service.enums.Genre;
import com.distributed_streaming_platform.content_service.enums.Language;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateContentRequest {

    private String title;

    private String description;

    private Genre genre;

    private Language language;

    private Integer duration;

    private LocalDate releaseDate;

    private String thumbnailUrl;

    private String trailerUrl;
}
