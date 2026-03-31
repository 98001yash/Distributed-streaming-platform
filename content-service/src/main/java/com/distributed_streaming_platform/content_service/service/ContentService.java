package com.distributed_streaming_platform.content_service.service;

import com.distributed_streaming_platform.content_service.dto.request.ContentFilterRequest;
import com.distributed_streaming_platform.content_service.dto.request.CreateContentRequest;
import com.distributed_streaming_platform.content_service.dto.request.UpdateContentRequest;
import com.distributed_streaming_platform.content_service.dto.response.ContentResponse;
import com.distributed_streaming_platform.content_service.dto.response.PagedContentResponse;
import com.distributed_streaming_platform.content_service.enums.ContentStatus;

public interface ContentService {

    //  Create content (ADMIN)
    ContentResponse createContent(CreateContentRequest request);

    //  Get single content
    ContentResponse getContentById(Long contentId);

    //  Get all content (pagination)
    PagedContentResponse getAllContent(int page, int size);

    // Filtered content (genre, language, type, status)
    PagedContentResponse filterContent(
            ContentFilterRequest filterRequest,
            int page,
            int size
    );

    // Update content (ADMIN)
    ContentResponse updateContent(
            Long contentId,
            UpdateContentRequest request
    );

    //  Delete content (ADMIN)
    void deleteContent(Long contentId);

    //  Update status (important for pipeline)
    void updateContentStatus(Long contentId, ContentStatus status);
}