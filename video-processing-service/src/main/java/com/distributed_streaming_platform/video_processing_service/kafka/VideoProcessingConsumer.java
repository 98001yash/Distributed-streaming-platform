package com.distributed_streaming_platform.video_processing_service.kafka;


import com.distributed_streaming_platform.events.VideoUploadedEvent;
import com.distributed_streaming_platform.video_processing_service.entity.ProcessedVideo;
import com.distributed_streaming_platform.video_processing_service.entity.VideoVariant;
import com.distributed_streaming_platform.video_processing_service.enums.ProcessingStatus;
import com.distributed_streaming_platform.video_processing_service.enums.VideoQuality;
import com.distributed_streaming_platform.video_processing_service.exceptions.VideoProcessingException;
import com.distributed_streaming_platform.video_processing_service.repository.ProcessedVideoRepository;
import com.distributed_streaming_platform.video_processing_service.repository.VideoVariantRepository;
import com.distributed_streaming_platform.video_processing_service.service.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoProcessingConsumer {


    private final VideoProcessingService videoProcessingService;

    @KafkaListener(
            topics = "video-uploaded",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(VideoUploadedEvent event, Acknowledgment ack) {

        log.info("Consuming video uploaded event {}", event);
        videoProcessingService.processVideo(event);
        ack.acknowledge();

    }

    private List<VideoVariant> simulateProcessing(
            ProcessedVideo processedVideo,
            VideoUploadedEvent event
    ) {

        List<VideoVariant> variants = new ArrayList<>();

        for (VideoQuality quality : VideoQuality.values()) {

            String url = buildVariantUrl(event.getObjectKey(), quality);

            VideoVariant variant = VideoVariant.builder()
                    .processedVideo(processedVideo)
                    .quality(quality)
                    .url(url)
                    .size(1000L)
                    .build();

            variants.add(variant);
        }

        return variants;
    }

    private String buildVariantUrl(String objectKey, VideoQuality quality) {
        return "processed/" + objectKey + "/" + quality.name().toLowerCase() + ".mp4";
    }
}
