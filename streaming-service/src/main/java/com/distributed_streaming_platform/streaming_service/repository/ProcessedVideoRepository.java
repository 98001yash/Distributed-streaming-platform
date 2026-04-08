package com.distributed_streaming_platform.streaming_service.repository;


import com.distributed_streaming_platform.streaming_service.entity.ProcessedVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedVideoRepository extends JpaRepository<ProcessedVideo, Long> {

    Optional<ProcessedVideo> findByContentId(Long contentId);

    Optional<ProcessedVideo> findByContentIdAndStatus(Long contentId, String status);

   // List<WatchHistory> findByUserId(Long userId);
}