package com.distributed_streaming_platform.streaming_service.controller;


import com.distributed_streaming_platform.streaming_service.dtos.StreamResponse;
import com.distributed_streaming_platform.streaming_service.service.StreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService streamingService;

    @GetMapping("/{contentId}")
    public StreamResponse streamVideo(@PathVariable Long contentId) {
        return streamingService.getStream(contentId);
    }


    @PostMapping("/{contentId}/progress")
    public void trackProgress(
            @PathVariable Long contentId,
            @RequestParam Long watchTime
    ) {
        streamingService.trackProgress(contentId, watchTime);
    }

    @PostMapping("/{contentId}/complete")
    public void markCompleted(@PathVariable Long contentId) {
        streamingService.markCompleted(contentId);
    }
}