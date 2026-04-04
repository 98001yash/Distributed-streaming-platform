package com.distributed_streaming_platform.video_processing_service.entity;


import com.distributed_streaming_platform.video_processing_service.enums.VideoQuality;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "video_variants",
        indexes = {
                @Index(name = "idx_processed_video_id", columnList = "processed_video_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_video_id", nullable = false)
    private ProcessedVideo processedVideo;

    @Enumerated(EnumType.STRING)
    private VideoQuality quality;


    @Column(nullable = false)
    private String url;

    private Long size;
}