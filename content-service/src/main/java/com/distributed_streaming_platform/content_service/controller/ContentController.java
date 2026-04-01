package com.distributed_streaming_platform.content_service.controller;

import com.distributed_streaming_platform.content_service.auth.RoleAllowed;
import com.distributed_streaming_platform.content_service.dto.request.ContentFilterRequest;
import com.distributed_streaming_platform.content_service.dto.request.CreateContentRequest;
import com.distributed_streaming_platform.content_service.dto.request.UpdateContentRequest;
import com.distributed_streaming_platform.content_service.dto.response.ContentResponse;
import com.distributed_streaming_platform.content_service.dto.response.PagedContentResponse;
import com.distributed_streaming_platform.content_service.enums.ContentStatus;
import com.distributed_streaming_platform.content_service.enums.ContentType;
import com.distributed_streaming_platform.content_service.enums.Genre;
import com.distributed_streaming_platform.content_service.enums.Language;
import com.distributed_streaming_platform.content_service.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ContentController {

    private final ContentService contentService;


    @RoleAllowed("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ContentResponse> createContent(
            @Valid @RequestBody CreateContentRequest request
    ) {
        log.info("API: Create content request received title={}", request.getTitle());

        ContentResponse response = contentService.createContent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> getContentById(
            @PathVariable Long id
    ) {
        log.info("API: Get content by id={}", id);
        return ResponseEntity.ok(contentService.getContentById(id));
    }


    @GetMapping
    public ResponseEntity<PagedContentResponse> getAllContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("API: Get all content page={} size={}", page, size);
        return ResponseEntity.ok(contentService.getAllContent(page, size));
    }


    @GetMapping("/filter")
    public ResponseEntity<PagedContentResponse> filterContent(
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) Language language,
            @RequestParam(required = false) ContentType type,
            @RequestParam(required = false) ContentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("API: Filter content genre={} language={} type={} status={}",
                genre, language, type, status);

        ContentFilterRequest filter = new ContentFilterRequest();
        filter.setGenre(genre);
        filter.setLanguage(language);
        filter.setType(type);
        filter.setStatus(status);

        return ResponseEntity.ok(
                contentService.filterContent(filter, page, size)
        );
    }


    @RoleAllowed("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ContentResponse> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateContentRequest request
    ) {
        log.info("API: Update content id={}", id);

        return ResponseEntity.ok(
                contentService.updateContent(id, request)
        );
    }



    @RoleAllowed("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long id
    ) {
        log.info("API: Delete content id={}", id);

        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }



    @RoleAllowed("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateContentStatus(
            @PathVariable Long id,
            @RequestParam ContentStatus status
    ) {
        log.info("API: Update content status id={} status={}", id, status);
        contentService.updateContentStatus(id, status);
        return ResponseEntity.ok().build();
    }
}