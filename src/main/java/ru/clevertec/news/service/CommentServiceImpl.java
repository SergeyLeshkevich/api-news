package ru.clevertec.news.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.clevertec.exceptionhandlerstarter.exception.AccessDeniedException;
import ru.clevertec.exceptionhandlerstarter.exception.MicroserviceResponseException;
import ru.clevertec.exceptionhandlerstarter.exception.ParsJsonException;
import ru.clevertec.exceptionhandlerstarter.model.IncorrectData;
import ru.clevertec.loggingstarter.annotation.Loggable;
import ru.clevertec.news.entity.dto.CommentRequest;
import ru.clevertec.news.entity.dto.CommentResponse;
import ru.clevertec.news.entity.dto.ModifyCommentRequest;
import ru.clevertec.news.entity.dto.UserRequest;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Service implementation for managing comments, providing operations such as retrieval, creation, updates, and archiving.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Service
@Loggable
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String HTTP_SCHEME = "http";
    private static final String COMMENT_SERVICE_HOST = "comment-service";
    private static final String COMMENTS_SEARCH_URL = "/comments/search";
    private static final String SEARCH_PARAM = "search";
    private static final String OFFSET_PARAM = "offset";
    private static final String LIMIT_PARAM = "limit";
    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String NUMBER_PAGE_PARAM = "numberPage";
    private static final String COMMENTS_NEWS_ID_URL = "/comments/news/{id}";
    private static final String COMMENTS_ID_URL = "/comments/{id}";
    private static final String COMMENTS_URL = "/comments";
    private static final String COMMENTS_ARCHIVE_URL = "/comments/archive";
    private static final String COMMENTS_ARCHIVE_ID_URL = "/comments/archive/{id}";
    private static final String COMMENTS_ARCHIVE_NEWS_ID_URL = "/comments/archive/news/{id}";
    private static final String COMMENTS_COMMENT_ID_NEWS_NEWS_ID_URL = "/comments/{commentId}/news/{newsId}";

    /**
     * WebClient builder for making HTTP requests.
     */
    private final WebClient.Builder webClientBuilder;

    /**
     * ObjectMapper for handling JSON serialization and deserialization.
     */
    private final ObjectMapper objectMapper;

    /**
     * Retrieves a specific comment by ID.
     *
     * @param id The ID of the comment to retrieve.
     * @return Mono containing ResponseEntity with the requested CommentResponse.
     */
    @Override
    public Mono<ResponseEntity<CommentResponse>> get(Long id) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder
                                .scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_ID_URL)
                                .build(id)
                )
                .exchangeToMono(getClientResponseMonoFunction());
    }

    /**
     * Retrieves a specific comment associated with a news item.
     *
     * @param commentId The ID of the comment.
     * @param newsId    The ID of the associated news item.
     * @return Mono containing ResponseEntity with the requested CommentResponse.
     */
    @Override
    public Mono<ResponseEntity<CommentResponse>> getCommentByNewsId(Long commentId, Long newsId) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_COMMENT_ID_NEWS_NEWS_ID_URL)
                                .build(commentId, newsId)
                )
                .exchangeToMono(getClientResponseMonoFunction());
    }

    /**
     * Retrieves paginated comments associated with a news item from the archive.
     *
     * @param idNews     The ID of the news item.
     * @param pageSize   Number of comments to retrieve per page.
     * @param numberPage Page number of comments to retrieve.
     * @return Mono containing ResponseEntity with PaginationResponse of CommentResponse.
     */
    @Override
    public Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getCommentsByNewsIdFromArchive(Long idNews,
                                                                                                    int pageSize,
                                                                                                    int numberPage) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_ARCHIVE_NEWS_ID_URL)
                                .queryParam(PAGE_SIZE_PARAM, pageSize)
                                .queryParam(NUMBER_PAGE_PARAM, numberPage)
                                .build(idNews)
                )
                .exchangeToMono(this::getResponseEntityMono);
    }

    /**
     * Retrieves a specific comment from the archive by ID.
     *
     * @param id The ID of the comment to retrieve from the archive.
     * @return Mono containing ResponseEntity with the requested CommentResponse.
     */
    @Override
    public Mono<ResponseEntity<CommentResponse>> getFromArchive(Long id) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_ARCHIVE_ID_URL)
                                .build(id)
                ).exchangeToMono(getClientResponseMonoFunction());
    }

    /**
     * Retrieves paginated comments.
     *
     * @param pageSize   Number of comments to retrieve per page.
     * @param numberPage Page number of comments to retrieve.
     * @return Mono containing ResponseEntity with PaginationResponse of CommentResponse.
     */
    @Override
    public Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAll(int pageSize, int numberPage) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_URL)
                                .queryParam(PAGE_SIZE_PARAM, pageSize)
                                .queryParam(NUMBER_PAGE_PARAM, numberPage)
                                .build()
                )
                .exchangeToMono(this::getResponseEntityMono);
    }

    /**
     * Retrieves paginated comments from the archive.
     *
     * @param pageSize   Number of comments to retrieve per page.
     * @param numberPage Page number of comments to retrieve.
     * @return Mono containing ResponseEntity with PaginationResponse of CommentResponse from the archive.
     */
    @Override
    public Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAllFromArchive(int pageSize, int numberPage) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_ARCHIVE_URL)
                                .queryParam(PAGE_SIZE_PARAM, pageSize)
                                .queryParam(NUMBER_PAGE_PARAM, numberPage)
                                .build()
                )
                .exchangeToMono(this::getResponseEntityMono);
    }

    /**
     * Creates a new comment.
     *
     * @param commentDto The CommentRequest object containing details of the comment to create.
     * @return Mono containing ResponseEntity with the created CommentResponse.
     */
    @Override
    public Mono<ResponseEntity<CommentResponse>> create(CommentRequest commentDto, HttpServletRequest request) {
        UUID userUuid = UUID.fromString(request.getHeader("X-User-UUID"));
        String userName = request.getHeader("X-User-Name");
        ModifyCommentRequest modifyCommentRequest = ModifyCommentRequest.builder()
                .text(commentDto.text())
                .newsId(commentDto.newsId())
                .user(new UserRequest(userUuid, userName))
                .build();

        return webClientBuilder.build().post()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_URL)
                                .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(modifyCommentRequest)
                .exchangeToMono(getClientResponseMonoFunction());
    }

    /**
     * Updates an existing comment.
     *
     * @param id         The ID of the comment to update.
     * @param commentDto The CommentRequest object containing updated details of the comment.
     * @return Mono containing ResponseEntity with the updated CommentResponse.
     */
    @Override
    public Mono<ResponseEntity<CommentResponse>> update(Long id, CommentRequest commentDto, HttpServletRequest request) {
        return webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder.scheme(HTTP_SCHEME)
                        .host(COMMENT_SERVICE_HOST)
                        .path(COMMENTS_ID_URL)
                        .build(id))
                .retrieve()
                .bodyToMono(ModifyCommentRequest.class)
                .flatMap(commentRequest -> {
                    UUID userUuid = UUID.fromString(request.getHeader("X-User-UUID"));
                    String userName = request.getHeader("X-User-Name");
                    ModifyCommentRequest modifyCommentRequest = ModifyCommentRequest.builder()
                            .text(commentDto.text())
                            .newsId(commentDto.newsId())
                            .user(new UserRequest(userUuid, userName))
                            .build();

                    if (!commentRequest.getUser().getUuid().equals(userUuid)) {
                        return Mono.error(new AccessDeniedException("No access rights"));
                    }
                    return webClientBuilder.build().put()
                            .uri(uriBuilder -> uriBuilder
                                    .scheme(HTTP_SCHEME)
                                    .host(COMMENT_SERVICE_HOST)
                                    .path(COMMENTS_ID_URL)
                                    .build(id))
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(modifyCommentRequest)
                            .exchangeToMono(getClientResponseMonoFunction());
                });
    }

    /**
     * Archives a comment by updating its status.
     *
     * @param id The ID of the comment to archive.
     * @return Mono containing ResponseEntity with Void.
     */
    @Override
    public Mono<ResponseEntity<Void>> archive(Long id, HttpServletRequest request) {

        return webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder.scheme(HTTP_SCHEME)
                        .host(COMMENT_SERVICE_HOST)
                        .path(COMMENTS_ID_URL)
                        .build(id))
                .retrieve()
                .bodyToMono(ModifyCommentRequest.class)
                .flatMap(commentRequest -> {
                    UUID userUuid = UUID.fromString(request.getHeader("X-User-UUID"));
                    if (!commentRequest.getUser().getUuid().equals(userUuid)) {
                        return Mono.error(new AccessDeniedException("No access rights"));
                    }
                    return webClientBuilder.build().patch()
                            .uri(uriBuilder ->
                                    uriBuilder.scheme(HTTP_SCHEME)
                                            .host(COMMENT_SERVICE_HOST)
                                            .path(COMMENTS_ID_URL)
                                            .build(id)
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .exchangeToMono(response -> {
                                if (response.statusCode().is2xxSuccessful()) {
                                    return response.bodyToMono(CommentResponse.class)
                                            .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.OK).build()));
                                } else {
                                    return response.createException().handle((body, sink) -> {
                                        IncorrectData incorrectData = body.getResponseBodyAs(IncorrectData.class);
                                        HttpStatusCode statusCode = body.getStatusCode();
                                        sink.error(new MicroserviceResponseException(incorrectData,
                                                HttpStatus.resolve(statusCode.value())));
                                    });
                                }
                            });
                });
    }

    /**
     * Archives comments associated with a news item by updating their status.
     *
     * @param newsId The ID of the news item.
     * @return Mono containing ResponseEntity with Void.
     */
    @Override
    public Mono<ResponseEntity<Void>> archiveByNewsId(Long newsId) {

        return webClientBuilder.build().patch()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_NEWS_ID_URL)
                                .build(newsId)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(CommentResponse.class)
                                .then(Mono.fromCallable(() -> ResponseEntity.status(HttpStatus.OK).build()));
                    } else {
                        return response.createException().handle((body, sink) -> {
                            IncorrectData incorrectData = body.getResponseBodyAs(IncorrectData.class);
                            HttpStatusCode statusCode = body.getStatusCode();
                            sink.error(new MicroserviceResponseException(incorrectData,
                                    HttpStatus.resolve(statusCode.value())));
                        });
                    }
                });
    }

    /**
     * Retrieves paginated comments associated with a news item.
     *
     * @param idNews     The ID of the news item.
     * @param pageSize   Number of comments to retrieve per page.
     * @param numberPage Page number of comments to retrieve.
     * @return Mono containing ResponseEntity with PaginationResponse of CommentResponse.
     */
    @Override
    public Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getCommentsByIdNews(Long idNews, int pageSize, int numberPage) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_NEWS_ID_URL)
                                .queryParam(PAGE_SIZE_PARAM, pageSize)
                                .queryParam(NUMBER_PAGE_PARAM, numberPage)
                                .build(idNews)
                )
                .exchangeToMono(this::getResponseEntityMono);
    }

    /**
     * Searches for comments based on the provided search criteria.
     *
     * @param searchValue The value to search for in comments.
     * @param offset      The offset for paginated results.
     * @param limit       The limit on the number of results to retrieve.
     * @return Mono containing ResponseEntity with a list of CommentResponse objects.
     */
    @Override
    public Mono<ResponseEntity<List<CommentResponse>>> search(String searchValue, Integer offset, Integer limit) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HTTP_SCHEME)
                                .host(COMMENT_SERVICE_HOST)
                                .path(COMMENTS_SEARCH_URL)
                                .queryParam(SEARCH_PARAM, searchValue)
                                .queryParam(OFFSET_PARAM, offset)
                                .queryParam(LIMIT_PARAM, limit)
                                .build()
                )
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(Object.class)
                                .handle((body, sink) -> {
                                    List<CommentResponse> responseList = null;
                                    try {
                                        String json = objectMapper.writeValueAsString(body);
                                        responseList = objectMapper.readValue(json,
                                                new TypeReference<List<CommentResponse>>() {
                                                });
                                    } catch (JsonProcessingException e) {
                                        sink.error(new ParsJsonException("Json comment is invalid"));
                                        return;
                                    }
                                    sink.next(ResponseEntity.ok().body(responseList));
                                });
                    } else {
                        return response.createException().handle((body, sink) -> {
                            IncorrectData incorrectData = body.getResponseBodyAs(IncorrectData.class);
                            HttpStatusCode statusCode = body.getStatusCode();
                            sink.error(new MicroserviceResponseException(incorrectData,
                                    HttpStatus.resolve(statusCode.value())));
                        });
                    }
                });
    }

    /**
     * Handles the conversion of ClientResponse to Mono of ResponseEntity containing PaginationResponse of CommentResponse.
     *
     * @param response The ClientResponse to be processed.
     * @return Mono of ResponseEntity containing PaginationResponse of CommentResponse.
     */
    private Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getResponseEntityMono(ClientResponse response) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(Object.class)
                    .handle((body, sink) -> {
                        PaginationResponse<CommentResponse> paginationResponse = null;
                        try {
                            String json = objectMapper.writeValueAsString(body);
                            paginationResponse = objectMapper.readValue(json,
                                    new TypeReference<PaginationResponse<CommentResponse>>() {
                                    });
                        } catch (JsonProcessingException e) {
                            sink.error(new ParsJsonException("Json comment is invalid"));
                            return;
                        }
                        sink.next(ResponseEntity.ok().body(paginationResponse));
                    });
        } else {
            return response.createException().handle((body, sink) -> {
                IncorrectData incorrectData = body.getResponseBodyAs(IncorrectData.class);
                HttpStatusCode statusCode = body.getStatusCode();
                sink.error(new MicroserviceResponseException(incorrectData,
                        HttpStatus.resolve(statusCode.value())));
            });
        }
    }

    /**
     * Handles the conversion of ClientResponse to Mono of ResponseEntity containing CommentResponse.
     *
     * @return Mono of ResponseEntity containing CommentResponse.
     */
    private Function<ClientResponse, Mono<ResponseEntity<CommentResponse>>> getClientResponseMonoFunction() {
        return response -> {
            if (response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(CommentResponse.class)
                        .map(body -> ResponseEntity.ok().body(body));
            } else {
                return response.createException().handle((body, sink) -> {
                    IncorrectData incorrectData = body.getResponseBodyAs(IncorrectData.class);
                    HttpStatusCode statusCode = body.getStatusCode();
                    sink.error(new MicroserviceResponseException(incorrectData, HttpStatus.resolve(statusCode.value())));
                });
            }
        };
    }
}
