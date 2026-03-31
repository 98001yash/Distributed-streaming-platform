package com.distributed_streaming_platform.content_service.dto.response;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedContentResponse {

    private List<ContentResponse> content;

    private int page;
    private int size;
    private long totalElements;
    private long totalPages;
    private boolean last;
}
