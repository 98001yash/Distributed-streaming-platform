package com.distributed_streaming_platform.content_service.kafka;


import com.distributed_streaming_platform.events.ContentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentEventProducer {


    private final KafkaTemplate<String, ContentCreatedEvent> kafkaTemplate;

    public void sendContentCreated(ContentCreatedEvent event){

        log.info("Publishing contentCreatedEvent for contentId={}",event.getContentId());
        kafkaTemplate.send(
                kafkaTopics.CONTENT_CREATED,
                String.valueOf(event.getContentId()),
                event
        );
    }
}
