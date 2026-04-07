package com.distributed_streaming_platform.streaming_service.service;

import com.distributed_streaming_platform.events.VideoStartedEvent;
import com.distributed_streaming_platform.streaming_service.auth.UserContextHolder;
import com.distributed_streaming_platform.streaming_service.dtos.StreamResponse;
import com.distributed_streaming_platform.streaming_service.entity.ProcessedVideo;
import com.distributed_streaming_platform.streaming_service.exceptions.StorageAccessException;
import com.distributed_streaming_platform.streaming_service.exceptions.UnauthorizedAccessException;
import com.distributed_streaming_platform.streaming_service.exceptions.VideoNotFoundException;
import com.distributed_streaming_platform.streaming_service.exceptions.VideoNotReadyException;
import com.distributed_streaming_platform.streaming_service.kafka.AnalyticsEventProducer;
import com.distributed_streaming_platform.streaming_service.repository.ProcessedVideoRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StreamingService {

    private final ProcessedVideoRepository processedVideoRepository;
    private final MinioClient minioClient;
    private final AnalyticsEventProducer analyticsEventProducer;

    @Value("${minio.bucket}")
    private String bucket;

    public StreamResponse getStream(Long contentId) {

        //  STEP 1: Get current user
        Long userId = UserContextHolder.getCurrentUserId();

        //  STEP 2: Fetch video
        ProcessedVideo video = processedVideoRepository.findByContentId(contentId)
                .orElseThrow(() -> new VideoNotFoundException(contentId));

        //  STEP 3: Check processing status
        if (!"COMPLETED".equalsIgnoreCase(video.getStatus())) {
            throw new VideoNotReadyException(contentId);
        }

        //  STEP 4: Ownership check
        if (!isOwnerOrAdmin(video, userId)) {
            throw new UnauthorizedAccessException();
        }

        //  STEP 5: Extract object key
        String objectKey = extractObjectKey(video.getMasterPlaylistUrl());

        //  STEP 6: Generate signed URL
        String signedUrl = generateSignedUrl(objectKey);

        //  STEP 7: Emit analytics event (CORRECT PLACE)
        publishVideoStartedEvent(userId, contentId);

        return StreamResponse.builder()
                .contentId(contentId)
                .streamUrl(signedUrl)
                .build();
    }

    /**
     * Ownership / Admin check
     */
    private boolean isOwnerOrAdmin(ProcessedVideo video, Long userId) {
        return video.getUploadedBy() != null && video.getUploadedBy().equals(userId);
    }

    /**
     * Extract object key from URL
     */
    private String extractObjectKey(String fullUrl) {
        int index = fullUrl.indexOf(bucket);
        return fullUrl.substring(index + bucket.length() + 1);
    }

    /**
     * Generate signed URL
     */
    private String generateSignedUrl(String objectKey) {

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(10, TimeUnit.MINUTES)
                            .build()
            );

        } catch (Exception e) {
            throw new StorageAccessException("Failed to generate signed URL", e);
        }
    }

    /**
     *  Publish analytics event
     */
    private void publishVideoStartedEvent(Long userId, Long contentId) {

        VideoStartedEvent event = VideoStartedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .userId(userId)
                .contentId(contentId)
                .build();

        analyticsEventProducer.sendVideoStartedEvent(event);
    }
}