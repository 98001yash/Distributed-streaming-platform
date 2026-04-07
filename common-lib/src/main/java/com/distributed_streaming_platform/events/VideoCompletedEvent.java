package com.distributed_streaming_platform.events;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoCompletedEvent {

    private String eventId;
    private LocalDateTime eventTime;

    private Long userId;
    private Long contentId;
}