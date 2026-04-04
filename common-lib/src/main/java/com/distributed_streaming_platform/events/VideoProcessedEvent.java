package com.distributed_streaming_platform.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoProcessedEvent {

    private Long eventId;
    private LocalDateTime eventTim;
    private Long contentId;

    private Map<String, String> variants;
    private String status;
}
