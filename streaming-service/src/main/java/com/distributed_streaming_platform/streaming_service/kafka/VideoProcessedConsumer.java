package com.distributed_streaming_platform.streaming_service.kafka;


import com.distributed_streaming_platform.events.VideoProcessedEvent;
import com.distributed_streaming_platform.streaming_service.entity.ProcessedVideo;
import com.distributed_streaming_platform.streaming_service.repository.ProcessedVideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoProcessedConsumer {

    private final ProcessedVideoRepository processedVideoRepository;

    @KafkaListener(
            topics = "video-processed",
            groupId = "streaming-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(VideoProcessedEvent event) {

        log.info("🔥 Consumed VideoProcessedEvent contentId={}", event.getContentId());

        if (processedVideoRepository.existsByContentId(event.getContentId())) {
            log.warn("Already exists contentId={}", event.getContentId());
            return;
        }

        ProcessedVideo video = new ProcessedVideo();

        video.setContentId(event.getContentId());
        video.setStatus(event.getStatus());
        video.setUploadedBy(event.getUploadedBy());

        //  Extract master playlist from variants
        String masterUrl = event.getVariants().get("P720"); // or choose best quality
        video.setMasterPlaylistUrl(masterUrl);

        processedVideoRepository.save(video);

        log.info("✅ Saved processed video in streaming DB contentId={}", event.getContentId());
    }
}