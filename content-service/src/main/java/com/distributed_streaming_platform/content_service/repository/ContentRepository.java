package com.distributed_streaming_platform.content_service.repository;

import com.distributed_streaming_platform.content_service.entity.Content;
import com.distributed_streaming_platform.content_service.enums.ContentStatus;
import com.distributed_streaming_platform.content_service.enums.ContentType;
import com.distributed_streaming_platform.content_service.enums.Genre;
import com.distributed_streaming_platform.content_service.enums.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface ContentRepository extends
        JpaRepository<Content, Long>,
        JpaSpecificationExecutor<Content> {

    Optional<Content> findByTitle(String title);

    boolean existsByTitle(String title);

    Page<Content> findByGenre(Genre genre, Pageable pageable);

    Page<Content> findByLanguage(Language language, Pageable pageable);

    Page<Content> findByType(ContentType type, Pageable pageable);

    Page<Content> findByStatus(ContentStatus status, Pageable pageable);
}
