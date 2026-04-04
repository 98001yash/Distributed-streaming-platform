package com.distributed_streaming_platform.video_processing_service.controller;


import com.distributed_streaming_platform.events.VideoUploadedEvent;
import com.distributed_streaming_platform.video_processing_service.entity.ProcessedVideo;
import com.distributed_streaming_platform.video_processing_service.entity.VideoVariant;
import com.distributed_streaming_platform.video_processing_service.service.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/video-processing")
@RequiredArgsConstructor
public class VideoProcessingController {

    private final VideoProcessingService videoProcessingService;


    @PostMapping("/process/{contentId}")
    public String processManually(@PathVariable Long contentId) {

        VideoUploadedEvent event = VideoUploadedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .contentId(contentId)
                .objectKey("videos/" + contentId + ".mp4") // consistent
                .storageUrl("http://localhost:9000/videos/" + contentId + ".mp4") //  correct
                .fileName(contentId + ".mp4")
                .build();

        videoProcessingService.processVideo(event);

        return "Processing started for contentId=" + contentId;
    }


    @GetMapping("/{contentId}")
    public ProcessedVideo getStatus(@PathVariable Long contentId) {
        return videoProcessingService.getByContentId(contentId);
    }


    @GetMapping("/{contentId}/variants")
    public List<VideoVariant> getVariants(@PathVariable Long contentId) {
        return videoProcessingService.getVariants(contentId);
    }
}