package com.distributed_streaming_platform.content_ingestion_service.entity;

import com.distributed_streaming_platform.content_ingestion_service.enums.UploadStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_assets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long contentId;

    @Column(nullable = false)
    private String fileName;

    private String contentType; // video/mp4

    private Long fileSize;

    @Column(nullable = false)
    private String storageUrl;

    private String bucketName;

    private String objectKey;


    @Enumerated(EnumType.STRING)
    private UploadStatus status;

    private Long uploadedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
