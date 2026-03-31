package com.distributed_streaming_platform.content_service.entity;

import com.distributed_streaming_platform.content_service.enums.ContentStatus;
import com.distributed_streaming_platform.content_service.enums.ContentType;
import com.distributed_streaming_platform.content_service.enums.Genre;
import com.distributed_streaming_platform.content_service.enums.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Info
    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    // Categorization
    @Enumerated(EnumType.STRING)
    private ContentType type;   // MOVIE / SERIES

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Enumerated(EnumType.STRING)
    private Language language;

    // Metadata
    private Integer duration; // in minutes

    private LocalDate releaseDate;

    // Lifecycle
    @Enumerated(EnumType.STRING)
    private ContentStatus status;

    // Media Info
    private String thumbnailUrl;

    private String trailerUrl;

    // Audit
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