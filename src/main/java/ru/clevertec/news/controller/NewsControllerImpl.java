package ru.clevertec.news.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;
import ru.clevertec.news.service.NewsService;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsControllerImpl implements NewsController {

    private final NewsService service;

    @Override
    public Mono<ResponseEntity<NewsResponse>> getById(Long id, int pageSizeComments, int numberPageComments) {
        return service.get(id, pageSizeComments, numberPageComments);
    }

    @Override
    public Mono<ResponseEntity<NewsResponse>> getFromArchive(Long id, int pageSizeComments, int numberPageComments) {
        return service.getFromArchive(id, pageSizeComments, numberPageComments);
    }

    @Override
    public Mono<ResponseEntity<PaginationResponse<NewsResponse>>> getAll(int pageSize, int numberPage) {
        return service.getAll(pageSize, numberPage);
    }

    @Override
    public Mono<ResponseEntity<PaginationResponse<NewsResponse>>> getAllFromArchive(int pageSize, int numberPage) {
        return service.getAllFromArchive(pageSize, numberPage);
    }

    @Override
    public Mono<ResponseEntity<NewsResponse>> create(NewsRequest newsDto,HttpServletRequest request) {
        return service.create(newsDto,request);
    }

    @Override
    public Mono<ResponseEntity<NewsResponse>> update(Long id, NewsRequest newsDto, HttpServletRequest request) {
        return service.update(id, newsDto, request);
    }

    @Override
    public Mono<ResponseEntity<Void>> moveToArchive(Long id, HttpServletRequest request) {
        return service.archive(id, request);
    }

    @Override
    public Mono<ResponseEntity<List<NewsResponse>>> search(String searchValue, Integer offset, Integer limit) {
        return service.search(searchValue, offset, limit);
    }
}
