package com.distributed_streaming_platform.analytic_service.kafka;


import com.distributed_streaming_platform.analytic_service.service.AnalyticsService;
import com.distributed_streaming_platform.events.VideoCompletedEvent;
import com.distributed_streaming_platform.events.VideoProgressEvent;
import com.distributed_streaming_platform.events.VideoStartedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "video-started",
            groupId = "analytics-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeVideoStarted(VideoStartedEvent event) {

        log.info("Consumed VideoStartedEvent contentId={} userId={}",
                event.getContentId(), event.getUserId());

        analyticsService.handleVideoStarted(event);
    }

    @KafkaListener(
            topics = "video-progress",
            groupId = "analytics-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeVideoProgress(VideoProgressEvent event) {

        log.info("Consumed VideoProgressEvent contentId={} watchTime={}",
                event.getContentId(), event.getWatchTime());

        analyticsService.handleVideoProgress(event);
    }

    @KafkaListener(
            topics = "video-completed",
            groupId = "analytics-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeVideoCompleted(VideoCompletedEvent event) {

        log.info("Consumed VideoCompletedEvent contentId={}",
                event.getContentId());

        analyticsService.handleVideoCompleted(event);
    }
}