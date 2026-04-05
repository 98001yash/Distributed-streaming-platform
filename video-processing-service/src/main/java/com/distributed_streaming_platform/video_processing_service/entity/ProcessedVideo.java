package com.distributed_streaming_platform.video_processing_service.entity;


import com.distributed_streaming_platform.video_processing_service.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "processed_videos",
        indexes = {
                @Index(name = "idx_video_id", columnList = "videoId")
        }
)
public class ProcessedVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long contentId;

    @Column(nullable = false)
    private String sourceUrl;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;

    private String errorMessage;

    private String masterPlaylistUrl;

    private Instant createdAt;
    private Instant updatedAt;


    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

}
