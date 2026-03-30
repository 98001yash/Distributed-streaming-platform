package com.distributed_streaming_platform.auth_service.kafka;


import com.distributed_streaming_platform.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    private static final String TOPIC = "user-created";

    public void sendUseCreatedEvent(UserCreatedEvent event){
        log.info("Sending UserCreatedEvent for userId={}",event.getUserId());
        kafkaTemplate.send(TOPIC, event);
    }
}
