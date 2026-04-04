package com.distributed_streaming_platform.video_processing_service.repository;


import com.distributed_streaming_platform.video_processing_service.entity.VideoVariant;
import com.distributed_streaming_platform.video_processing_service.enums.VideoQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoVariantRepository extends JpaRepository<VideoVariant, Long> {


    List<VideoVariant> findByProcessedVideoId(Long processedVideoId);
    List<VideoVariant> findByProcessedVideoIdAndQuality(Long processedVideoId, VideoQuality quality);
    boolean existsByProcessedVideoIdAndQuality(Long processedVideoId, VideoQuality quality);
}