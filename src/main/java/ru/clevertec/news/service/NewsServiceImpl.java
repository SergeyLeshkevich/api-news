package ru.clevertec.news.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.clevertec.exceptionhandlerstarter.entity.IncorrectData;
import ru.clevertec.exceptionhandlerstarter.exception.AccessDeniedException;
import ru.clevertec.exceptionhandlerstarter.exception.MicroserviceResponseException;
import ru.clevertec.exceptionhandlerstarter.exception.ParsJsonException;
import ru.clevertec.loggingstarter.annotation.Loggable;
import ru.clevertec.news.entity.dto.CommentResponse;
import ru.clevertec.news.entity.dto.ModifyCommentRequest;
import ru.clevertec.news.entity.dto.ModifyNewsRequest;
import ru.clevertec.news.entity.dto.NewsRequest;
import ru.clevertec.news.entity.dto.NewsResponse;
import ru.clevertec.news.entity.dto.UserRequest;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;


/**
 * Service implementation for managing news, providing operations such as retrieval, creation, updates, and archiving.
 * Utilizes the WebClient for making HTTP requests to the 'news-service'.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Service
@Loggable
public class NewsServiceImpl implements NewsService {

    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String NUMBER_PAGE_PARAM = "numberPage";
    private static final String SEARCH_PARAM = "search";
    private static final String OFFSET_PARAM = "offset";
    private static final String LIMIT_PARAM = "limit";
    private static final String NEWS_URL = "/news";
    private static final String NEWS_ARCHIVE_ID_URL = "/news/archive/{id}";
    private static final String NEWS_ARCHIVE_URL = "/news/archive";
    private static final String NEWS_ID_URL = "/news/{id}";
    private static final String NEWS_SEARCH_URL = "/news/search";
    private static final String X_USER_UUID = "X-User-UUID";
    private static final String X_USER_NAME = "X-User-Name";

    /**
     * WebClient builder for making HTTP requests.
     */
    private final WebClient.Builder webClientBuilder;

    /**
     * Service for managing comments related to news.
     */
    private final CommentService commentService;

    /**
     * ObjectMapper for handling JSON serialization and deserialization.
     */
    private final ObjectMapper objectMapper;

    public NewsServiceImpl(@Qualifier("webClientBuilderNewsUrl")WebClient.Builder webClientBuilder,
                           CommentService commentService,
                           ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.commentService = commentService;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves a specific news item along with its associated comments.
     *
     * @param id                 The ID of the news item to retrieve.
     * @param pageSizeComments   Number of comments to retrieve per page.
     * @param numberPageComments Page number of comments to retrieve.
     * @return Mono containing ResponseEntity with NewsResponse and associated comments.
     */
    public Mono<ResponseEntity<NewsResponse>> get(Long id, int pageSizeComments, int numberPageComments) {

        Mono<ResponseEntity<NewsResponse>> monoNews = webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.path(NEWS_ID_URL)
                                .build(id))
                .exchangeToMono(getClientResponseMonoFunction());

        Mono<ResponseEntity<PaginationResponse<CommentResponse>>> monoComments = commentService
                .getCommentsByIdNews(id, pageSizeComments, numberPageComments);

        return getZip(monoNews, monoComments);
    }

    /**
     * Retrieves a specific archived news item along with its associated comments.
     *
     * @param id                 The ID of the news item to retrieve from the archive.
     * @param pageSizeComments   Number of comments to retrieve per page.
     * @param numberPageComments Page number of comments to retrieve.
     * @return Mono containing ResponseEntity with NewsResponse and associated comments from the archive.
     */
    @Override
    public Mono<ResponseEntity<NewsResponse>> getFromArchive(Long id, int pageSizeComments, int numberPageComments) {

        Mono<ResponseEntity<NewsResponse>> monoNewsFromArchive = webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.path(NEWS_ARCHIVE_ID_URL)
                                .build(id))
                .exchangeToMono(getClientResponseMonoFunction());
        Mono<ResponseEntity<PaginationResponse<CommentResponse>>> monoComments = commentService
                .getCommentsByNewsIdFromArchive(id, pageSizeComments, numberPageComments);

        return getZip(monoNewsFromArchive, monoComments);
    }

    /**
     * Retrieves a paginated list of all news items.
     *
     * @param pageSize   Number of news items to retrieve per page.
     * @param numberPage Page number of news items to retrieve.
     * @return Mono containing ResponseEntity with a paginated list of NewsResponse objects.
     */
    @Override
    public Mono<ResponseEntity<PaginationResponse<NewsResponse>>> getAll(int pageSize, int numberPage) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.path(NEWS_URL)
                                .queryParam(PAGE_SIZE_PARAM, pageSize)
                                .queryParam(NUMBER_PAGE_PARAM, numberPage)
                                .build())
                .exchangeToMono(this::getResponseEntityMono);
    }

    /**
     * Retrieves a paginated list of all archived news items.
     *
     * @param pageSize   Number of archived news items to retrieve per page.
     * @param numberPage Page number of archived news items to retrieve.
     * @return Mono containing ResponseEntity with a paginated list of NewsResponse objects from the archive.
     */
    @Override
    public Mono<ResponseEntity<PaginationResponse<NewsResponse>>> getAllFromArchive(int pageSize, int numberPage) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.path(NEWS_ARCHIVE_URL)
                                .queryParam(PAGE_SIZE_PARAM, pageSize)
                                .queryParam(NUMBER_PAGE_PARAM, numberPage)
                                .build())
                .exchangeToMono(this::getResponseEntityMono);
    }

    /**
     * Creates a new news item.
     *
     * @param newsDto The NewsRequest object containing details of the news item to create.
     * @return Mono containing ResponseEntity with the created NewsResponse.
     */
    @Override
    public Mono<ResponseEntity<NewsResponse>> create(NewsRequest newsDto, HttpServletRequest request) {
        UUID userUuid = UUID.fromString(request.getHeader(X_USER_UUID));
        String userName = request.getHeader(X_USER_NAME);
        ModifyNewsRequest modifyNewsRequest = ModifyNewsRequest.builder().
                title(newsDto.title())
                .text(newsDto.text())
                .user(new UserRequest(userUuid, userName))
                .build();

        return webClientBuilder.build().post()
                .uri(uriBuilder ->
                        uriBuilder.path(NEWS_URL)
                                .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(modifyNewsRequest)
                .exchangeToMono(getClientResponseMonoFunction());
    }

    /**
     * Updates an existing news item.
     *
     * @param id      The ID of the news item to update.
     * @param newsDto The NewsRequest object containing updated details of the news item.
     * @return Mono containing ResponseEntity with the updated NewsResponse.
     */
    @Override
    public Mono<ResponseEntity<NewsResponse>> update(Long id, NewsRequest newsDto, HttpServletRequest request) {

        return webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder.path(NEWS_ID_URL)
                        .build(id))
                .retrieve()
                .bodyToMono(ModifyNewsRequest.class)
                .flatMap(newsRequest -> {
                    UUID userUuid = UUID.fromString(request.getHeader(X_USER_UUID));
                    String userName = request.getHeader(X_USER_NAME);
                    ModifyNewsRequest modifyNewsRequest = ModifyNewsRequest.builder().
                            title(newsDto.title())
                            .text(newsDto.text())
                            .user(new UserRequest(userUuid, userName))
                            .build();

                    if (!newsRequest.getUser().getUuid().equals(userUuid)) {
                        return Mono.error(new AccessDeniedException("No access rights"));
                    }
                    return webClientBuilder.build().put()
                            .uri(uriBuilder ->
                                    uriBuilder.path(NEWS_ID_URL)
                                            .build(id)
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(modifyNewsRequest)
                            .exchangeToMono(getClientResponseMonoFunction());
                });
    }

    /**
     * Archives a news item by updating its status and archives associated comments.
     *
     * @param id The ID of the news item to archive.
     * @return Mono containing ResponseEntity with Void.
     */
    @Override
    public Mono<ResponseEntity<Void>> archive(Long id, HttpServletRequest request) {

        Mono<ResponseEntity<Void>> responseEntityMonoNews = webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder.path(NEWS_ID_URL)
                        .build(id))
                .retrieve()
                .bodyToMono(ModifyCommentRequest.class)
                .flatMap(commentRequest -> {
                    UUID userUuid = UUID.fromString(request.getHeader(X_USER_UUID));
                    if (!commentRequest.getUser().getUuid().equals(userUuid)) {
                        return Mono.error(new AccessDeniedException("No access rights"));
                    }
                    return webClientBuilder.build().patch()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .path(NEWS_ID_URL)
                                            .build(id)
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .exchangeToMono(response -> {
                                if (response.statusCode().is2xxSuccessful()) {
                                    return response.bodyToMono(Void.class)
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

        Mono<ResponseEntity<Void>> responseEntityMonoComment = commentService.archiveByNewsId(id);

        return Mono.zip(responseEntityMonoNews, responseEntityMonoComment)
                .flatMap(chain -> Mono.just(chain.getT1()));
    }

    /**
     * Searches for news items based on the provided search criteria.
     *
     * @param searchValue The value to search for in news items.
     * @param offset      The offset for paginated results.
     * @param limit       The limit on the number of results to retrieve.
     * @return Mono containing ResponseEntity with a list of NewsResponse objects.
     */
    @Override
    public Mono<ResponseEntity<List<NewsResponse>>> search(String searchValue, Integer offset, Integer limit) {
        return webClientBuilder.build().get()
                .uri(uriBuilder ->
                        uriBuilder.path(NEWS_SEARCH_URL)
                                .queryParam(SEARCH_PARAM, searchValue)
                                .queryParam(OFFSET_PARAM, offset)
                                .queryParam(LIMIT_PARAM, limit)
                                .build()
                )
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(Object.class)
                                .handle((body, sink) -> {
                                    List<NewsResponse> responseList;
                                    try {
                                        String json = objectMapper.writeValueAsString(body);
                                        responseList = objectMapper.readValue(json,
                                                new TypeReference<List<NewsResponse>>() {
                                                });
                                    } catch (JsonProcessingException e) {
                                        sink.error(new ParsJsonException("Json news is invalid"));
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
     * Handles the conversion of ClientResponse to Mono of ResponseEntity containing PaginationResponse of NewsResponse.
     *
     * @param response The ClientResponse to be processed.
     * @return Mono of ResponseEntity containing PaginationResponse of NewsResponse.
     */
    private Mono<ResponseEntity<PaginationResponse<NewsResponse>>> getResponseEntityMono(ClientResponse response) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(Object.class)
                    .handle((body, sink) -> {
                        PaginationResponse<NewsResponse> paginationResponse;
                        try {
                            String json = objectMapper.writeValueAsString(body);
                            paginationResponse = objectMapper.readValue(json,
                                    new TypeReference<PaginationResponse<NewsResponse>>() {
                                    });
                        } catch (JsonProcessingException e) {
                            sink.error(new ParsJsonException("Json news is invalid"));
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
     * Handles the conversion of ClientResponse to Mono of ResponseEntity containing NewsResponse.
     *
     * @return Mono of ResponseEntity containing NewsResponse.
     */
    private Function<ClientResponse, Mono<ResponseEntity<NewsResponse>>> getClientResponseMonoFunction() {
        return response -> {
            if (response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(NewsResponse.class)
                        .map(body -> ResponseEntity.ok().body(body));
            } else {
                return response.createException().handle((body, sink) -> {
                    IncorrectData incorrectData = body.getResponseBodyAs(IncorrectData.class);
                    HttpStatusCode statusCode = body.getStatusCode();
                    sink.error(new MicroserviceResponseException(incorrectData,
                            HttpStatus.resolve(statusCode.value())));
                });
            }
        };
    }

    /**
     * Combines Mono of ResponseEntity of NewsResponse and Mono of ResponseEntity of PaginationResponse of CommentResponse
     * into a single Mono of ResponseEntity of NewsResponse with associated comments.
     *
     * @param monoNews     Mono of ResponseEntity of NewsResponse.
     * @param monoComments Mono of ResponseEntity of PaginationResponse of CommentResponse.
     * @return Mono of ResponseEntity of NewsResponse with associated comments.
     */
    private Mono<ResponseEntity<NewsResponse>> getZip(
            Mono<ResponseEntity<NewsResponse>> monoNews,
            Mono<ResponseEntity<PaginationResponse<CommentResponse>>> monoComments) {
        return Mono.zip(monoNews, monoComments, (news, comments) -> {
            PaginationResponse<CommentResponse> commentsBody = comments.getBody();
            if (commentsBody != null && news.getBody() != null) {
                news.getBody().setComments(commentsBody);
            }
            return news;
        });
    }
}
