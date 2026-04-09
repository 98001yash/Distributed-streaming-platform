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
public class VideoUploadedEvent {

    private String eventId;
    private LocalDateTime eventTime;
    private Long contentId;

    private String objectKey;
    private String storageUrl;
    private String fileName;

    private Long uploadedBy;
}
