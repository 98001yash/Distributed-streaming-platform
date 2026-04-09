package com.distributed_streaming_platform.video_processing_service.kafka;



import com.distributed_streaming_platform.events.VideoProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoProcessedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "video-processed";

    public void send(VideoProcessedEvent event) {
        log.info("Sending VideoProcessedEvent for contentId={}", event.getContentId());

        kafkaTemplate.send(TOPIC, event.getContentId().toString(), event);
    }
}