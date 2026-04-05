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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoProcessingService {

    private final ProcessedVideoRepository processedVideoRepository;
    private final VideoVariantRepository videoVariantRepository;

    private final FFmpegService ffmpegService;
    private final StorageService storageService;

    public void processVideo(VideoUploadedEvent event) {

        log.info("Processing video for contentId={}", event.getContentId());

        //  Idempotency
        if (processedVideoRepository.existsByContentId(event.getContentId())) {
            log.warn("Already processed contentId={}", event.getContentId());
            return;
        }

        //  Create DB record
        ProcessedVideo processedVideo = ProcessedVideo.builder()
                .contentId(event.getContentId())
                .sourceUrl(event.getStorageUrl())
                .status(ProcessingStatus.PROCESSING)
                .build();

        processedVideo = processedVideoRepository.save(processedVideo);

        try {
            //  REAL PROCESSING
            List<VideoVariant> variants = processWithFFmpeg(processedVideo, event);

            videoVariantRepository.saveAll(variants);

            processedVideo.setStatus(ProcessingStatus.COMPLETED);
            processedVideoRepository.save(processedVideo);

            log.info("Processing completed for contentId={}", event.getContentId());

        } catch (Exception e) {

            log.error("Processing failed for contentId={}", event.getContentId(), e);

            processedVideo.setStatus(ProcessingStatus.FAILED);
            processedVideoRepository.save(processedVideo);

            throw new RuntimeException("Video processing failed", e);
        }
    }

    /**
     *  CORE PROCESSING LOGIC
     */
    private List<VideoVariant> processWithFFmpeg(
            ProcessedVideo processedVideo,
            VideoUploadedEvent event
    ) throws Exception {

        //  Step 1: Download original video
        File inputFile = storageService.downloadToLocal(event.getObjectKey());

        List<VideoVariant> variants = new ArrayList<>();

        try {

            for (VideoQuality quality : VideoQuality.values()) {

                //  Step 2: Transcode
                File outputFile = ffmpegService.transcode(inputFile, quality.name());

                //  Step 3: Build correct path
                String objectKey = buildVariantObjectKey(event.getObjectKey(), quality);

                // ☁ Step 4: Upload to MinIO
                String url = storageService.uploadFile(objectKey, outputFile);

                //  Step 5: Save entity
                variants.add(VideoVariant.builder()
                        .processedVideo(processedVideo)
                        .quality(quality)
                        .url(url)
                        .size(outputFile.length())
                        .build());

                //  Cleanup output file
                outputFile.delete();
            }

        } finally {
            //  Cleanup input file
            inputFile.delete();
        }

        return variants;
    }

    /**
     *  Builds variant path using SAME structure as ingestion
     */
    private String buildVariantObjectKey(String objectKey, VideoQuality quality) {

        String basePath = objectKey.substring(0, objectKey.lastIndexOf("/") + 1);

        return basePath + quality.name().toLowerCase() + ".mp4";
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