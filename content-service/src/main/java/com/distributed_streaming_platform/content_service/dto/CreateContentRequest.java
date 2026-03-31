package com.distributed_streaming_platform.content_service.dto;


import com.distributed_streaming_platform.content_service.enums.ContentType;
import com.distributed_streaming_platform.content_service.enums.Genre;
import com.distributed_streaming_platform.content_service.enums.Language;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateContentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 2000, message = "Description too long")
    private String description;

    @NotNull(message = "Content type is required")
    private ContentType type;

    @NotNull(message = "Genre is required")
    private Genre genre;

    @NotNull(message = "Language is required")
    private Language language;

    @Min(value = 1, message = "Duration must be positive")
    private Integer duration;

    private LocalDate releaseDate;

    private String thumbnailUrl;

    private String trailerUrl;
}