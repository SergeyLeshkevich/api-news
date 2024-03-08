package ru.clevertec.news.service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import ru.clevertec.news.entity.dto.CommentResponse;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;

public interface NewsService {

    Mono<ResponseEntity<NewsResponse>> get(Long id, int pageSizeComments, int numberPageComments);

    Mono<ResponseEntity<NewsResponse>> getFromArchive(Long id, int pageSizeComments, int numberPageComments);

    Mono<ResponseEntity<PaginationResponse<NewsResponse>>> getAll(int pageSize, int numberPage);

    Mono<ResponseEntity<PaginationResponse<NewsResponse>>> getAllFromArchive(int pageSize, int numberPage);

    Mono<ResponseEntity<NewsResponse>> create(NewsRequest newsDto, HttpServletRequest request);

    Mono<ResponseEntity<NewsResponse>> update(Long id, NewsRequest newsDto, HttpServletRequest request);

    Mono<ResponseEntity<Void>> archive(Long id, HttpServletRequest request);
    Mono<ResponseEntity<List<NewsResponse>>> search(String searchValue, Integer offset, Integer limit);
}
