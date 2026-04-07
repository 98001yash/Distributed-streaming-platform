package com.distributed_streaming_platform.analytic_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "watch_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 user
    @Column(nullable = false)
    private Long userId;

    // 🎬 content
    @Column(nullable = false)
    private Long contentId;

    private Long watchTime;

    private String status;

    private LocalDateTime startedAt;
    private LocalDateTime lastUpdatedAt;
}