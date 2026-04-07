package com.distributed_streaming_platform.events;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoProgressEvent {

    private String eventId;
    private LocalDateTime eventTime;

    private Long userId;
    private Long contentId;

    private Long watchTime; // seconds
}