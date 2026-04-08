package com.distributed_streaming_platform.analytic_service.service;


import com.distributed_streaming_platform.analytic_service.dtos.WatchHistoryResponse;
import com.distributed_streaming_platform.analytic_service.entity.WatchHistory;
import com.distributed_streaming_platform.analytic_service.exceptions.AnalyticsDataNotFoundException;
import com.distributed_streaming_platform.analytic_service.repository.WatchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsQueryService {

    private final WatchHistoryRepository watchHistoryRepository;

    public List<WatchHistoryResponse> getUserHistory(Long userId) {

        List<WatchHistory> historyList = watchHistoryRepository.findByUserId(userId);

        if (historyList.isEmpty()) {
            throw new AnalyticsDataNotFoundException("No watch history found for user");
        }

        return historyList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public WatchHistoryResponse getVideoHistory(Long userId, Long contentId) {

        WatchHistory history = watchHistoryRepository
                .findByUserIdAndContentId(userId, contentId)
                .orElseThrow(() -> new AnalyticsDataNotFoundException("History not found"));

        return mapToResponse(history);
    }

    private WatchHistoryResponse mapToResponse(WatchHistory history) {
        return WatchHistoryResponse.builder()
                .contentId(history.getContentId())
                .watchTime(history.getWatchTime())
                .status(history.getStatus())
                .startedAt(history.getStartedAt())
                .lastUpdatedAt(history.getLastUpdatedAt())
                .build();
    }
}