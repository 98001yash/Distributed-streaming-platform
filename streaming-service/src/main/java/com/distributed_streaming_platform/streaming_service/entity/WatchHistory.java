package com.distributed_streaming_platform.streaming_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "watch_history",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "content_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 USER
    @Column(name = "user_id", nullable = false)
    private Long userId;

    //  CONTENT
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    // ⏱ WATCH TIME (SECONDS)
    @Column(name = "watch_time")
    private Long watchTime;

    //  STATUS
    @Column(name = "status")
    private String status;

    //  START TIME
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    //  LAST UPDATE
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}