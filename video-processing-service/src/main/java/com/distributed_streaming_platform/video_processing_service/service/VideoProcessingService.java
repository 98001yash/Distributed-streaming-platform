package com.distributed_streaming_platform.video_processing_service.service;

import com.distributed_streaming_platform.events.VideoProcessedEvent;
import com.distributed_streaming_platform.events.VideoUploadedEvent;
import com.distributed_streaming_platform.video_processing_service.entity.ProcessedVideo;
import com.distributed_streaming_platform.video_processing_service.entity.VideoVariant;
import com.distributed_streaming_platform.video_processing_service.enums.ProcessingStatus;
import com.distributed_streaming_platform.video_processing_service.enums.VideoQuality;
import com.distributed_streaming_platform.video_processing_service.kafka.VideoProcessedProducer;
import com.distributed_streaming_platform.video_processing_service.repository.ProcessedVideoRepository;
import com.distributed_streaming_platform.video_processing_service.repository.VideoVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoProcessingService {

    private final ProcessedVideoRepository processedVideoRepository;
    private final VideoVariantRepository videoVariantRepository;
    private final FFmpegService ffmpegService;
    private final StorageService storageService;
    private final VideoProcessedProducer videoProcessedProducer;

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

            // ✅ PROCESS VIDEO
            processHLS(processedVideo, event);

            processedVideo.setStatus(ProcessingStatus.COMPLETED);
            processedVideoRepository.save(processedVideo);

            log.info("Processing completed for contentId={}", event.getContentId());

            // 🚀🔥 SEND EVENT HERE
            sendVideoProcessedEvent(processedVideo);

        } catch (Exception e) {

            log.error("Processing failed for contentId={}", event.getContentId(), e);

            processedVideo.setStatus(ProcessingStatus.FAILED);
            processedVideoRepository.save(processedVideo);

            // 🚀 OPTIONAL: send FAILED event also
            sendVideoProcessedEvent(processedVideo);

            throw new RuntimeException("Video processing failed", e);
        }
    }

    private void processHLS(ProcessedVideo processedVideo, VideoUploadedEvent event) throws Exception {

        File inputFile = storageService.downloadToLocal(event.getObjectKey());

        try {

            //  STEP 1: PROCESS EACH QUALITY
            for (VideoQuality quality : VideoQuality.values()) {

                String tempDir = System.getProperty("java.io.tmpdir")
                        + "\\hls-" + event.getContentId() + "-" + quality.name();

                File folder = ffmpegService.generateHLS(inputFile, quality.name(), tempDir);

                String baseObjectPath = "hls/" + event.getContentId() + "/" + quality.name();

                String playlistUrl = null;

                List<File> filesToDelete = new ArrayList<>();

                for (File file : folder.listFiles()) {

                    String objectKey = baseObjectPath + "/" + file.getName();

                    log.info("Uploading file to MinIO: {}", objectKey);

                    String url = storageService.uploadFile(objectKey, file);

                    if (file.getName().endsWith(".m3u8")) {
                        playlistUrl = url;
                    }

                    filesToDelete.add(file);
                }

                // cleanup
                for (File file : filesToDelete) {
                    file.delete();
                }
                folder.delete();

                // save DB
                videoVariantRepository.save(
                        VideoVariant.builder()
                                .processedVideo(processedVideo)
                                .quality(quality)
                                .url(playlistUrl)
                                .size(0L)
                                .build()
                );
            }

            //  STEP 2: CREATE MASTER PLAYLIST
            String masterDir = System.getProperty("java.io.tmpdir")
                    + "\\hls-master-" + event.getContentId();

            new File(masterDir).mkdirs();

            File masterFile = createMasterPlaylist(event.getContentId(), masterDir);

            //  STEP 3: UPLOAD MASTER PLAYLIST
            String masterObjectKey = "hls/" + event.getContentId() + "/master.m3u8";

            String masterUrl = storageService.uploadFile(masterObjectKey, masterFile);

            log.info("Master playlist uploaded: {}", masterUrl);

            // cleanup
            masterFile.delete();
            new File(masterDir).delete();

            // OPTIONAL: store master URL (BEST PRACTICE)
            processedVideo.setMasterPlaylistUrl(masterUrl);
            processedVideoRepository.save(processedVideo);

        } finally {
            inputFile.delete();
        }
    }

    private File createMasterPlaylist(Long contentId, String baseDir) throws Exception {

        File masterFile = new File(baseDir + "\\master.m3u8");

        StringBuilder content = new StringBuilder();
        content.append("#EXTM3U\n\n");

        for (VideoQuality quality : VideoQuality.values()) {

            String resolution = switch (quality) {
                case P240 -> "426x240";
                case P480 -> "854x480";
                case P720 -> "1280x720";
                case P1080 -> "1920x1080";
            };

            int bandwidth = switch (quality) {
                case P240 -> 800000;
                case P480 -> 1400000;
                case P720 -> 2800000;
                case P1080 -> 5000000;
            };

            content.append("#EXT-X-STREAM-INF:BANDWIDTH=")
                    .append(bandwidth)
                    .append(",RESOLUTION=")
                    .append(resolution)
                    .append("\n")
                    .append(quality.name())
                    .append("/")
                    .append(quality.name())
                    .append(".m3u8\n\n");
        }

        try (FileWriter writer = new FileWriter(masterFile)) {
            writer.write(content.toString());
        }

        return masterFile;
    }

    public ProcessedVideo getByContentId(Long contentId) {
        return processedVideoRepository.findByContentId(contentId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
    }

    public List<VideoVariant> getVariants(Long contentId) {
        ProcessedVideo video = getByContentId(contentId);
        return videoVariantRepository.findByProcessedVideoId(video.getId());
    }

    private void sendVideoProcessedEvent(ProcessedVideo processedVideo) {

        VideoProcessedEvent event = VideoProcessedEvent.builder()
                .eventId(processedVideo.getId())
                .eventTim(java.time.LocalDateTime.now())
                .contentId(processedVideo.getContentId())
                .status(processedVideo.getStatus().name())
                .variants(getVariantMap(processedVideo.getContentId()))
                .build();

        videoProcessedProducer.send(event);
    }

    private Map<String, String> getVariantMap(Long contentId) {

        List<VideoVariant> variants =
                videoVariantRepository.findByProcessedVideoId(
                        processedVideoRepository.findByContentId(contentId).get().getId()
                );

        Map<String, String> map = new HashMap<>();

        for (VideoVariant variant : variants) {
            map.put(variant.getQuality().name(), variant.getUrl());
        }

        return map;
    }
}