package com.distributed_streaming_platform.content_ingestion_service.service.Impl;


import com.distributed_streaming_platform.content_ingestion_service.auth.UserContextHolder;
import com.distributed_streaming_platform.content_ingestion_service.dtos.VideoUploadResponse;
import com.distributed_streaming_platform.content_ingestion_service.entity.VideoAsset;
import com.distributed_streaming_platform.content_ingestion_service.enums.UploadStatus;
import com.distributed_streaming_platform.content_ingestion_service.exceptions.*;
import com.distributed_streaming_platform.content_ingestion_service.kafka.VideoEventProducer;
import com.distributed_streaming_platform.content_ingestion_service.repository.VideoAssetRepository;
import com.distributed_streaming_platform.content_ingestion_service.service.StorageService;
import com.distributed_streaming_platform.content_ingestion_service.service.VideoUploadService;
import com.distributed_streaming_platform.events.VideoUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoUploadServiceImpl implements VideoUploadService {

    private final VideoAssetRepository videoAssetRepository;
    private final StorageService storageService;
    private final VideoEventProducer videoEventProducer;

    private static final long MAX_FILE_SIZE = 500 * 1024 * 1024; // 500MB

    @Override
    public VideoUploadResponse uploadVideo(Long contentId, MultipartFile file) {

        Long userId = UserContextHolder.getCurrentUserId();

        log.info("User {} uploading video for contentId={}", userId, contentId);



        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (!file.getContentType().startsWith("video/")) {
            throw new InvalidFileTypeException(file.getContentType());
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException(file.getSize());
        }

        if (videoAssetRepository.existsByContentId(contentId)) {
            throw new DuplicateUploadException(contentId);
        }

        String objectKey = generateObjectKey(contentId, file.getOriginalFilename());

        String storageUrl;

        try {
            storageUrl = storageService.uploadFile(objectKey,file);
        } catch (Exception ex) {
            log.error("Storage failed for contentId={}", contentId, ex);
            throw new StorageException("Failed to upload file", ex);
        }


        VideoAsset asset = VideoAsset.builder()
                .contentId(contentId)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .objectKey(objectKey)
                .storageUrl(storageUrl)
                .status(UploadStatus.UPLOADED)
                .uploadedBy(userId)
                .build();

        VideoAsset saved = videoAssetRepository.save(asset);

        log.info("Video uploaded successfully assetId={}", saved.getId());



        publishVideoUploadedEvent(saved);
        return VideoUploadResponse.builder()
                .assetId(saved.getId())
                .contentId(saved.getContentId())
                .fileName(saved.getFileName())
                .storageUrl(saved.getStorageUrl())
                .status(saved.getStatus())
                .uploadedAt(saved.getCreatedAt())
                .build();
    }

    private String generateObjectKey(Long contentId, String fileName) {
        return "videos/" + contentId + "/" + UUID.randomUUID() + "-" + fileName;
    }

    private void publishVideoUploadedEvent(VideoAsset asset) {

        VideoUploadedEvent event = VideoUploadedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .contentId(asset.getContentId())
                .objectKey(asset.getObjectKey())
                .storageUrl(asset.getStorageUrl())
                .fileName(asset.getFileName())
                .uploadedBy(UserContextHolder.getCurrentUserId())
                .build();

        videoEventProducer.sendVideoUploadedEvent(event);
    }

}
