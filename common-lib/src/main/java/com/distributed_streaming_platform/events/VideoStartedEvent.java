package com.distributed_streaming_platform.events;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoStartedEvent {

    private String eventId;
    private LocalDateTime eventTime;

    private Long userId;
    private Long contentId;
}