package com.distributed_streaming_platform.streaming_service.entity;




import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "processed_videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedVideo {

    @Id
    private Long id;

    //  Used to fetch video
    @Column(nullable = false, unique = true)
    private Long contentId;

    // PROCESSING / COMPLETED / FAILED
    @Column(nullable = false)
    private String status;

    //  MASTER PLAYLIST (MOST IMPORTANT)
    @Column(name = "master_playlist_url")
    private String masterPlaylistUrl;

    //  OWNER OF CONTENT (for access control)
    @Column(name = "uploaded_by")
    private Long uploadedBy;
}