package com.distributed_streaming_platform.video_processing_service.repository;

import com.distributed_streaming_platform.video_processing_service.entity.ProcessedVideo;
import com.distributed_streaming_platform.video_processing_service.enums.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProcessedVideoRepository extends JpaRepository<ProcessedVideo, Long> {

    Optional<ProcessedVideo> findByContentId(Long contentId);

    boolean existsByContentId(Long contentId);
    Optional<ProcessedVideo> findByContentIdAndStatus(Long contentId, ProcessingStatus status);
}