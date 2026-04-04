package com.distributed_streaming_platform.video_processing_service.service;


import com.distributed_streaming_platform.events.VideoUploadedEvent;
import com.distributed_streaming_platform.video_processing_service.entity.ProcessedVideo;
import com.distributed_streaming_platform.video_processing_service.entity.VideoVariant;
import com.distributed_streaming_platform.video_processing_service.enums.ProcessingStatus;
import com.distributed_streaming_platform.video_processing_service.enums.VideoQuality;
import com.distributed_streaming_platform.video_processing_service.repository.ProcessedVideoRepository;
import com.distributed_streaming_platform.video_processing_service.repository.VideoVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoProcessingService {

    private final ProcessedVideoRepository processedVideoRepository;
    private final VideoVariantRepository videoVariantRepository;


    public void processVideo(VideoUploadedEvent event) {

        log.info("Processing video for contentId={}", event.getContentId());

        if (processedVideoRepository.existsByContentId(event.getContentId())) {
            log.warn("Already processed contentId={}", event.getContentId());
            return;
        }

        ProcessedVideo processedVideo = ProcessedVideo.builder()
                .contentId(event.getContentId())
                .sourceUrl(event.getStorageUrl())
                .status(ProcessingStatus.PROCESSING)
                .build();

        processedVideo = processedVideoRepository.save(processedVideo);

        List<VideoVariant> variants = generateVariants(processedVideo, event);

        videoVariantRepository.saveAll(variants);

        processedVideo.setStatus(ProcessingStatus.COMPLETED);
        processedVideoRepository.save(processedVideo);

        log.info("Processing completed for contentId={}", event.getContentId());
    }

    private List<VideoVariant> generateVariants(
            ProcessedVideo processedVideo,
            VideoUploadedEvent event
    ) {
        List<VideoVariant> variants = new ArrayList<>();

        for (VideoQuality quality : VideoQuality.values()) {

            String url = "processed/" + event.getObjectKey() + "/" + quality.name() + ".mp4";

            variants.add(VideoVariant.builder()
                    .processedVideo(processedVideo)
                    .quality(quality)
                    .url(url)
                    .size(1000L)
                    .build());
        }

        return variants;
    }

    public ProcessedVideo getByContentId(Long contentId) {
        return processedVideoRepository.findByContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
    }

    public List<VideoVariant> getVariants(Long contentId) {
        ProcessedVideo video = getByContentId(contentId);
        return videoVariantRepository.findByProcessedVideoId(video.getId());
    }
}
