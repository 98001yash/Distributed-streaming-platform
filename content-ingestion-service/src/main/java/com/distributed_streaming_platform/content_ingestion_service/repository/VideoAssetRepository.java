package com.distributed_streaming_platform.content_ingestion_service.repository;

import com.distributed_streaming_platform.content_ingestion_service.entity.VideoAsset;
import com.distributed_streaming_platform.content_ingestion_service.enums.UploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface VideoAssetRepository extends JpaRepository<VideoAsset, Long> {

    Optional<VideoAsset> findByContentId(Long contentId);

    boolean existsByContentId(Long contentId);
    Optional<VideoAsset> findByContentIdAndStatus(Long contentId, UploadStatus status);

    List<VideoAsset> findByStatus(UploadStatus status);
    List<VideoAsset> findByUploadedBy(Long userId);
}