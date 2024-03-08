package ru.clevertec.news.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.clevertec.news.entity.dto.CommentRequest;
import ru.clevertec.news.entity.dto.CommentResponse;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentControllerImpl implements CommentController {

    private final CommentService service;

    @Override
    public Mono<ResponseEntity<CommentResponse>> getById(Long id) {
        return service.get(id);
    }

    @Override
    public Mono<ResponseEntity<CommentResponse>> getByIdNews(Long idComment, Long idNews) {
        return service.getCommentByNewsId(idComment, idNews);
    }

    @Override
    public Mono<ResponseEntity<CommentResponse>> getFromArchive(Long id) {
        return service.getFromArchive(id);
    }

    @Override
    public Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAll(int pageSize, int numberPage) {
        return service.getAll(pageSize, numberPage);
    }

    @Override
    public Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAllByIdNews(Long idNews, int pageSize, int numberPage) {
        return service.getCommentsByIdNews(idNews, pageSize, numberPage);
    }

    @Override
    public Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAllFromArchive(int pageSize, int numberPage) {
        return service.getAllFromArchive(pageSize, numberPage);
    }

    @Override
    public Mono<ResponseEntity<CommentResponse>> create(CommentRequest commentDto, HttpServletRequest request) {
        return service.create(commentDto,request);
    }

    @Override
    public Mono<ResponseEntity<CommentResponse>> update(Long id, CommentRequest commentDto, HttpServletRequest request) {
        return service.update(id, commentDto, request);
    }


    @Override
    public Mono<ResponseEntity<Void>> moveToArchive(Long id, HttpServletRequest request) {
        return service.archive(id, request);
    }

    @Override
    public Mono<ResponseEntity<List<CommentResponse>>> search(String searchValue, Integer offset, Integer limit) {
        return service.search(searchValue, offset, limit);
    }
}
