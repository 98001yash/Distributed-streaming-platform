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

        // Idempotency
        if (processedVideoRepository.existsByContentId(event.getContentId())) {
            log.warn("Already processed contentId={}", event.getContentId());
            return;
        }

        //  Create processing record
        ProcessedVideo processedVideo = ProcessedVideo.builder()
                .contentId(event.getContentId())
                .sourceUrl(event.getStorageUrl())
                .status(ProcessingStatus.PROCESSING)
                .build();

        processedVideo = processedVideoRepository.save(processedVideo);

        try {

            //  MAIN LOGIC
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
     *  CORE HLS PIPELINE
     */
    private void processHLS(
            ProcessedVideo processedVideo,
            VideoUploadedEvent event
    ) throws Exception {

        //  Download original video
        File inputFile = storageService.downloadToLocal(event.getObjectKey());

        try {

            for (VideoQuality quality : VideoQuality.values()) {

                log.info("Processing quality={} for contentId={}", quality, event.getContentId());

                // Temp folder per quality
                String tempDir = System.getProperty("java.io.tmpdir")
                        + "\\hls-" + event.getContentId() + "-" + quality.name();

                File folder = ffmpegService.generateHLS(inputFile, quality.name(), tempDir);

                String baseObjectPath = "hls/" + event.getContentId() + "/" + quality.name();

                String playlistUrl = null;

                List<File> filesToDelete = new ArrayList<>();

                //  Upload ALL generated files
                for (File file : folder.listFiles()) {

                    String objectKey = baseObjectPath + "/" + file.getName();

                    log.info("Uploading file to MinIO: {}", objectKey);

                    String url = storageService.uploadFile(objectKey, file);

                    // Save ONLY playlist URL
                    if (file.getName().endsWith(".m3u8")) {
                        playlistUrl = url;
                    }

                    filesToDelete.add(file);
                }

                //  DELETE FILES AFTER ALL UPLOADS
                for (File file : filesToDelete) {
                    file.delete();
                }

                folder.delete();

                //  Save ONE DB entry per quality
                videoVariantRepository.save(
                        VideoVariant.builder()
                                .processedVideo(processedVideo)
                                .quality(quality)
                                .url(playlistUrl)
                                .size(0L)
                                .build()
                );

                log.info("Completed upload for quality={}", quality);
            }

        } finally {
            inputFile.delete(); // cleanup input
        }
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