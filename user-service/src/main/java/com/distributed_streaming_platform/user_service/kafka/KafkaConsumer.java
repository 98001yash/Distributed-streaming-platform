package com.distributed_streaming_platform.user_service.kafka;


import com.distributed_streaming_platform.events.UserCreatedEvent;
import com.distributed_streaming_platform.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final UserService userService;

    @KafkaListener(topics = "user-created",
            groupId = "user-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(UserCreatedEvent event){

        log.info("Received UserCreatedEvent for userId={}",event.getUserId());
        userService.createUser(event.getUserId(), event.getEmail());
    }
}
