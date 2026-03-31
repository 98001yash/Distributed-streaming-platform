package com.distributed_streaming_platform.content_service.service.Impl;


import com.distributed_streaming_platform.ContentCreatedEvent;
import com.distributed_streaming_platform.content_service.auth.UserContextHolder;
import com.distributed_streaming_platform.content_service.dto.request.ContentFilterRequest;
import com.distributed_streaming_platform.content_service.dto.request.CreateContentRequest;
import com.distributed_streaming_platform.content_service.dto.request.UpdateContentRequest;
import com.distributed_streaming_platform.content_service.dto.response.ContentResponse;
import com.distributed_streaming_platform.content_service.dto.response.PagedContentResponse;
import com.distributed_streaming_platform.content_service.entity.Content;
import com.distributed_streaming_platform.content_service.enums.ContentStatus;
import com.distributed_streaming_platform.content_service.exceptions.ContentAlreadyExistsException;
import com.distributed_streaming_platform.content_service.exceptions.ContentNotFoundException;
import com.distributed_streaming_platform.content_service.kafka.ContentEventProducer;
import com.distributed_streaming_platform.content_service.repository.ContentRepository;
import com.distributed_streaming_platform.content_service.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.criteria.Predicate;
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentServiceImpl implements ContentService {

    private  final ContentRepository contentRepository;
    private final ContentEventProducer contentEventProducer;


    @Override
    public ContentResponse createContent(CreateContentRequest request) {


        Long userId = UserContextHolder.getCurrentUserId();
        log.info("User {} creating content with title={}",userId, request.getTitle());

        if(contentRepository.existsByTitle(request.getTitle())){
            throw new ContentAlreadyExistsException(request.getTitle());
        }

        Content content = Content.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .genre(request.getGenre())
                .language(request.getLanguage())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .status(ContentStatus.UPLOADED)
                .thumbnailUrl(request.getThumbnailUrl())
                .trailerUrl(request.getTrailerUrl())
                .build();

        Content saved = contentRepository.save(content);

        log.info("Content created successfully with id={}",saved.getId());

        publishContentCreatedEvent(saved);

        // Publish event
        return mapToResponse(saved);
    }

    @Override
    public ContentResponse getContentById(Long contentId) {


        log.info("Fetching content with id={}",contentId);
        Content content=  contentRepository.findById(contentId)
                .orElseThrow(()->new ContentNotFoundException(contentId));

        return mapToResponse(content);
    }

    @Override
    public PagedContentResponse getAllContent(int page, int size) {

        log.info("Fetching all content page={}, size={}",page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Content> contentPage = contentRepository.findAll(pageable);
        return mapToPagedResponse(contentPage);
    }

    @Override
    public PagedContentResponse filterContent(ContentFilterRequest filterRequest, int page, int size) {
        return null;
    }

    @Override
    public ContentResponse updateContent(Long contentId, UpdateContentRequest request) {
        return null;
    }

    @Override
    public void deleteContent(Long contentId) {

    }

    @Override
    public void updateContentStatus(Long contentId, ContentStatus status) {

    }


    private void publishContentCreatedEvent(Content content) {

        ContentCreatedEvent event = ContentCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventTime(LocalDateTime.now())
                .contentId(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .genre(content.getGenre().name())
                .language(content.getLanguage().name())
                .type(content.getType().name())
                .status(content.getStatus().name())
                .thumbnailUrl(content.getThumbnailUrl())
                .createdAt(content.getCreatedAt())
                .build();

        contentEventProducer.sendContentCreated(event);
    }

    private ContentResponse mapToResponse(Content content) {
        return ContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .type(content.getType())
                .genre(content.getGenre())
                .language(content.getLanguage())
                .duration(content.getDuration())
                .releaseDate(content.getReleaseDate())
                .status(content.getStatus())
                .thumbnailUrl(content.getThumbnailUrl())
                .trailerUrl(content.getTrailerUrl())
                .createdAt(content.getCreatedAt())
                .build();
    }

    private PagedContentResponse mapToPagedResponse(Page<Content> page) {
        return PagedContentResponse.builder()
                .content(page.getContent().stream().map(this::mapToResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    private Specification<Content> buildSpecification(ContentFilterRequest filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getGenre() != null) {
                predicates.add(cb.equal(root.get("genre"), filter.getGenre()));
            }
            if (filter.getLanguage() != null) {
                predicates.add(cb.equal(root.get("language"), filter.getLanguage()));
            }
            if (filter.getType() != null) {
                predicates.add(cb.equal(root.get("type"), filter.getType()));
            }
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
