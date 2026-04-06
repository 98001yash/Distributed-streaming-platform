package com.distributed_streaming_platform.streaming_service.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    private Long contentId;

    private String status;

    private String masterPlaylistUrl;
}
