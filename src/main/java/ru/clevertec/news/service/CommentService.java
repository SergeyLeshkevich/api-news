package ru.clevertec.news.service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import ru.clevertec.news.entity.dto.CommentRequest;
import ru.clevertec.news.entity.dto.CommentResponse;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;


public interface CommentService {

    Mono<ResponseEntity<CommentResponse>> get(Long id);

    Mono<ResponseEntity<CommentResponse>> getCommentByNewsId(Long commentId, Long newsId);

    Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getCommentsByNewsIdFromArchive(Long idNews, int pageSize, int numberPage);


    Mono<ResponseEntity<CommentResponse>> getFromArchive(Long id);

    Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAll(int pageSize, int numberPage);

    Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAllFromArchive(int pageSize, int numberPage);


    Mono<ResponseEntity<CommentResponse>> create(CommentRequest commentDto, HttpServletRequest request);

    Mono<ResponseEntity<CommentResponse>> update(Long id, CommentRequest commentDto, HttpServletRequest request);

    Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getCommentsByIdNews(Long idNews, int pageSize, int numberPage);

    Mono<ResponseEntity<Void>> archive(Long id, HttpServletRequest request);

    Mono<ResponseEntity<Void>> archiveByNewsId(Long newsId);

    Mono<ResponseEntity<List<CommentResponse>>> search(String searchValue, Integer offset, Integer limit);
}
