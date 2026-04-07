package com.distributed_streaming_platform.analytic_service.repository;


import com.distributed_streaming_platform.analytic_service.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {

    Optional<WatchHistory> findByUserIdAndContentId(Long userId, Long contentId);
}
