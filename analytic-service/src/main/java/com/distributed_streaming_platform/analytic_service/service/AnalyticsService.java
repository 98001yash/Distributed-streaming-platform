package com.distributed_streaming_platform.analytic_service.service;


import com.distributed_streaming_platform.analytic_service.entity.WatchHistory;
import com.distributed_streaming_platform.analytic_service.repository.WatchHistoryRepository;
import com.distributed_streaming_platform.events.VideoCompletedEvent;
import com.distributed_streaming_platform.events.VideoProgressEvent;
import com.distributed_streaming_platform.events.VideoStartedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final WatchHistoryRepository watchHistoryRepository;

    //  START EVENT
    public void handleVideoStarted(VideoStartedEvent event) {

        WatchHistory history = watchHistoryRepository
                .findByUserIdAndContentId(event.getUserId(), event.getContentId())
                .orElse(
                        WatchHistory.builder()
                                .userId(event.getUserId())
                                .contentId(event.getContentId())
                                .watchTime(0L)
                                .status("STARTED")
                                .startedAt(LocalDateTime.now())
                                .build()
                );

        history.setLastUpdatedAt(LocalDateTime.now());

        watchHistoryRepository.save(history);

        log.info("Video started tracked contentId={}", event.getContentId());
    }

    // ⏱ PROGRESS EVENT
    public void handleVideoProgress(VideoProgressEvent event) {

        WatchHistory history = watchHistoryRepository
                .findByUserIdAndContentId(event.getUserId(), event.getContentId())
                .orElseThrow(() -> new RuntimeException("Watch history not found"));

        history.setWatchTime(event.getWatchTime());
        history.setStatus("IN_PROGRESS");
        history.setLastUpdatedAt(LocalDateTime.now());

        watchHistoryRepository.save(history);

        log.info("Video progress updated contentId={} watchTime={}",
                event.getContentId(), event.getWatchTime());
    }

    //  COMPLETED EVENT
    public void handleVideoCompleted(VideoCompletedEvent event) {

        WatchHistory history = watchHistoryRepository
                .findByUserIdAndContentId(event.getUserId(), event.getContentId())
                .orElseThrow(() -> new RuntimeException("Watch history not found"));

        history.setStatus("COMPLETED");
        history.setLastUpdatedAt(LocalDateTime.now());

        watchHistoryRepository.save(history);

        log.info("Video completed contentId={}", event.getContentId());
    }
}
