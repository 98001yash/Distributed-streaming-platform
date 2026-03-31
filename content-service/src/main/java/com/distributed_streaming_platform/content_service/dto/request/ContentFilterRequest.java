package com.distributed_streaming_platform.content_service.dto.request;


import com.distributed_streaming_platform.content_service.enums.ContentStatus;
import com.distributed_streaming_platform.content_service.enums.ContentType;
import com.distributed_streaming_platform.content_service.enums.Genre;
import com.distributed_streaming_platform.content_service.enums.Language;
import lombok.Data;

@Data
public class ContentFilterRequest {

    private Genre genre;

    private Language language;

    private ContentType type;

    private ContentStatus status;
}
