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

        try {

            //  USE HLS ONLY
            processHLS(processedVideo, event);

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

    private void processHLS(
            ProcessedVideo processedVideo,
            VideoUploadedEvent event
    ) throws Exception {

        File inputFile = storageService.downloadToLocal(event.getObjectKey());

        try {

            for (VideoQuality quality : VideoQuality.values()) {

                String tempDir = System.getProperty("java.io.tmpdir")
                        + "\\hls-" + event.getContentId() + "-" + quality.name();

                File folder = ffmpegService.generateHLS(inputFile, quality.name(), tempDir);

                String baseObjectPath = "hls/" + event.getContentId() + "/" + quality.name();

                String playlistUrl = null;

                for (File file : folder.listFiles()) {

                    String objectKey = baseObjectPath + "/" + file.getName();

                    String url = storageService.uploadFile(objectKey, file);

                    // 🎯 Save ONLY playlist
                    if (file.getName().endsWith(".m3u8")) {
                        playlistUrl = url;
                    }

                    file.delete(); // cleanup
                }

                // 💾 Save ONE entry per quality
                videoVariantRepository.save(
                        VideoVariant.builder()
                                .processedVideo(processedVideo)
                                .quality(quality)
                                .url(playlistUrl)
                                .size(0L)
                                .build()
                );

                folder.delete();
            }

        } finally {
            inputFile.delete();
        }
    }
}