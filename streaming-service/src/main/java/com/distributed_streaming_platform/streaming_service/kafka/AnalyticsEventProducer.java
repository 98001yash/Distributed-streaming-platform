package com.distributed_streaming_platform.streaming_service.kafka;



import com.distributed_streaming_platform.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String STARTED_TOPIC = "video-started";
    private static final String PROGRESS_TOPIC = "video-progress";
    private static final String COMPLETED_TOPIC = "video-completed";

    public void sendVideoStartedEvent(VideoStartedEvent event) {
        kafkaTemplate.send(STARTED_TOPIC, event.getContentId().toString(), event);
    }

    public void sendVideoProgressEvent(VideoProgressEvent event) {
        kafkaTemplate.send(PROGRESS_TOPIC, event.getContentId().toString(), event);
    }

    public void sendVideoCompletedEvent(VideoCompletedEvent event) {
        kafkaTemplate.send(COMPLETED_TOPIC, event.getContentId().toString(), event);
    }
}