package com.distributed_streaming_platform.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentCreatedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private Long contentId;
    private String title;
    private String description;
    private String genre;
    private String language;
    private String type;
    private String status;

    private String thumbnailUrl;
    private LocalDateTime createdAt;
}
