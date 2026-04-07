package com.distributed_streaming_platform.streaming_service.kafka;


import com.distributed_streaming_platform.events.VideoStartedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "video-started";

    public void sendVideoStartedEvent(VideoStartedEvent event) {

        log.info("Publishing VideoStartedEvent contentId={} userId={}",
                event.getContentId(), event.getUserId());

        kafkaTemplate.send(TOPIC, event.getContentId().toString(), event);
    }
}
