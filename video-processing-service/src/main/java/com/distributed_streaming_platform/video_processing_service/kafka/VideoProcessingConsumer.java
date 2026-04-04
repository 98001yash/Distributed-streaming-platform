package com.distributed_streaming_platform.video_processing_service.kafka;


import com.distributed_streaming_platform.events.VideoUploadedEvent;
import com.distributed_streaming_platform.video_processing_service.entity.ProcessedVideo;
import com.distributed_streaming_platform.video_processing_service.entity.VideoVariant;
import com.distributed_streaming_platform.video_processing_service.enums.ProcessingStatus;
import com.distributed_streaming_platform.video_processing_service.enums.VideoQuality;
import com.distributed_streaming_platform.video_processing_service.exceptions.VideoProcessingException;
import com.distributed_streaming_platform.video_processing_service.repository.ProcessedVideoRepository;
import com.distributed_streaming_platform.video_processing_service.repository.VideoVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoProcessingConsumer {

    private final ProcessedVideoRepository processedVideoRepository;
    private final VideoVariantRepository videoVariantRepository;



    @KafkaListener(
            topics = "video-uploaded",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(VideoUploadedEvent event) {

        log.info("Received VideoUploadedEvent: contentId={}, eventId={}",
                event.getContentId(), event.getEventId());

        try {
            //  IDEMPOTENCY CHECK
            if (processedVideoRepository.existsByContentId(event.getContentId())) {
                log.warn("Video already processed, skipping contentId={}", event.getContentId());
                return;
            }

            //  STEP 1: CREATE PROCESS RECORD
            ProcessedVideo processedVideo = ProcessedVideo.builder()
                    .contentId(event.getContentId())
                    .sourceUrl(event.getStorageUrl())
                    .status(ProcessingStatus.PROCESSING)
                    .build();

            processedVideo = processedVideoRepository.save(processedVideo);

            // ⚙ STEP 2: PROCESS VIDEO (SIMULATION)
            List<VideoVariant> variants = simulateProcessing(processedVideo, event);

            //  STEP 3: SAVE VARIANTS
            videoVariantRepository.saveAll(variants);

            //  STEP 4: UPDATE STATUS
            processedVideo.setStatus(ProcessingStatus.COMPLETED);
            processedVideoRepository.save(processedVideo);

            log.info("Video processing completed for contentId={}", event.getContentId());

        } catch (Exception e) {
            log.error("Error processing video for contentId={}", event.getContentId(), e);

            throw new VideoProcessingException(
                    "Failed to process video for contentId=" + event.getContentId()
            );
        }
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
