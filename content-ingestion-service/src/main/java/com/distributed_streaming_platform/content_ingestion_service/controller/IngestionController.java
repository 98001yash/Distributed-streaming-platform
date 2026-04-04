package com.distributed_streaming_platform.content_ingestion_service.controller;

import com.distributed_streaming_platform.content_ingestion_service.auth.RoleAllowed;
import com.distributed_streaming_platform.content_ingestion_service.dtos.VideoUploadResponse;
import com.distributed_streaming_platform.content_ingestion_service.service.VideoUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ingestion")
@RequiredArgsConstructor
@Slf4j
@Validated
public class IngestionController {

    private final VideoUploadService videoUploadService;


    @RoleAllowed("hasRole('ADMIN')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoUploadResponse> uploadVideo(
            @RequestParam("contentId") Long contentId,
            @RequestPart("file") MultipartFile file
    ) {

        log.info("API: Upload video request contentId={}", contentId);

        VideoUploadResponse response =
                videoUploadService.uploadVideo(contentId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
