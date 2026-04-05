package com.distributed_streaming_platform.video_processing_service.controller;


import com.distributed_streaming_platform.events.VideoUploadedEvent;
import com.distributed_streaming_platform.video_processing_service.entity.ProcessedVideo;
import com.distributed_streaming_platform.video_processing_service.entity.VideoVariant;
import com.distributed_streaming_platform.video_processing_service.service.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/video-processing")
@RequiredArgsConstructor
public class VideoProcessingController {

    private final VideoProcessingService videoProcessingService;

    //  Debug endpoint using real event
    @PostMapping("/process")
    public String processManually(@RequestBody VideoUploadedEvent event) {
        videoProcessingService.processVideo(event);
        return "Processing started for contentId=" + event.getContentId();
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