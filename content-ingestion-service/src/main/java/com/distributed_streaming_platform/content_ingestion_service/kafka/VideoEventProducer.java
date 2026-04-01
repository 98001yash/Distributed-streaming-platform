package com.distributed_streaming_platform.content_ingestion_service.kafka;

import com.distributed_streaming_platform.events.VideoUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoEventProducer {

    private final KafkaTemplate<String, VideoUploadedEvent> kafkaTemplate;

    public void sendVideoUploadedEvent(VideoUploadedEvent event) {

        String key = String.valueOf(event.getContentId());

        log.info("Publishing VideoUploadedEvent for contentId={}", event.getContentId());

        kafkaTemplate.send(KafkaTopics.VIDEO_UPLOADED, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Event sent successfully for contentId={}", event.getContentId());
                    } else {
                        log.error("Failed to send VideoUploadedEvent for contentId={}", event.getContentId(), ex);
                    }
                });
    }
}