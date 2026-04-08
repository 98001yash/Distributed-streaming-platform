package com.distributed_streaming_platform.analytic_service.controller;


import com.distributed_streaming_platform.analytic_service.auth.UserContextHolder;
import com.distributed_streaming_platform.analytic_service.dtos.WatchHistoryResponse;
import com.distributed_streaming_platform.analytic_service.service.AnalyticsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsQueryService analyticsQueryService;

    //  GET ALL WATCH HISTORY
    @GetMapping("/history")
    public List<WatchHistoryResponse> getUserHistory() {

        Long userId = UserContextHolder.getCurrentUserId();

        return analyticsQueryService.getUserHistory(userId);
    }

    //  GET SPECIFIC VIDEO HISTORY
    @GetMapping("/{contentId}")
    public WatchHistoryResponse getVideoHistory(@PathVariable Long contentId) {

        Long userId = UserContextHolder.getCurrentUserId();

        return analyticsQueryService.getVideoHistory(userId, contentId);
    }
}