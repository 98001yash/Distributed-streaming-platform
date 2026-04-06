package com.distributed_streaming_platform.streaming_service.controller;


import com.distributed_streaming_platform.streaming_service.auth.RoleAllowed;
import com.distributed_streaming_platform.streaming_service.dtos.StreamResponse;
import com.distributed_streaming_platform.streaming_service.service.StreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stream")
@RequiredArgsConstructor
public class StreamingController {

    private final StreamingService streamingService;

    @RoleAllowed("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{contentId}")
    public StreamResponse streamVideo(@PathVariable Long contentId) {
        return streamingService.getStream(contentId);
    }
}