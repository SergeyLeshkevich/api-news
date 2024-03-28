package ru.clevertec.news.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import ru.clevertec.exceptionhandlerstarter.exception.AccessDeniedException;
import ru.clevertec.news.config.Config;
import ru.clevertec.news.entity.dto.CommentRequest;
import ru.clevertec.news.entity.dto.CommentResponse;
import ru.clevertec.news.entity.dto.ModifyCommentRequest;
import ru.clevertec.news.entity.dto.UserRequest;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.util.CommentRequestTestBuilder;
import ru.clevertec.news.util.CommentResponseTestBuilder;
import ru.clevertec.news.util.PaginationResponse;
import ru.clevertec.news.util.PaginationResponseForCommentTestBuilder;
import ru.clevertec.news.util.UserRequestBuilderTest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest
@WireMockTest(httpPort = 9998)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Import(Config.class)
class CommentIntegrationTest {

    @Mock
    HttpServletRequest httpServletRequest;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CommentService commentService;

    @Autowired
    @Qualifier("webClientBuilderCommentsUrl")
    WebClient.Builder webClientBuilder;

    @Test
    void shouldRetrieveCommentWhenStatusCodeIs2xx() throws JsonProcessingException {
        Long commentId = 1L;
        CommentResponse commentResponse = CommentResponseTestBuilder.aCommentResponse().build();
        String expected = objectMapper.writeValueAsString(commentResponse);

        stubFor(get(urlEqualTo("/comments/" + commentId))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        ResponseEntity<CommentResponse> actual = commentService.get(commentId).block();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getText()).isEqualTo("Test text comment");

    }

    @Test
    void shouldRetrieveCommentByNewsIdWhenStatusCodeIs2xx() throws JsonProcessingException {
        Long commentId = 1L;
        Long newsId = 1L;
        CommentResponse commentResponse = CommentResponseTestBuilder.aCommentResponse().build();
        String expected = objectMapper.writeValueAsString(commentResponse);

        stubFor(get(urlEqualTo("/comments/" + commentId + "/news/" + newsId))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        ResponseEntity<CommentResponse> actual = commentService.getCommentByNewsId(commentId, newsId).block();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getText()).isEqualTo("Test text comment");

    }

    @Test
    void shouldRetrieveCommentByNewsIdFromArchiveWhenStatusCodeIs2xx() throws JsonProcessingException {
        Long newsId = 1L;
        int pageSize = 1;
        int numberPage = 1;

        PaginationResponse<CommentResponse> paginationResponse = PaginationResponseForCommentTestBuilder
                .aPaginationResponse()
                .build();
        String expected = objectMapper.writeValueAsString(paginationResponse);

        stubFor(get(urlEqualTo("/comments/archive/news/" + newsId + "?pageSize=1&numberPage=1"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        ResponseEntity<PaginationResponse<CommentResponse>> actual = commentService.getCommentsByNewsIdFromArchive(newsId, pageSize, numberPage).block();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getContent().size()).isOne();
        assertThat(actual.getBody().getCountPage()).isEqualTo(paginationResponse.getCountPage());
        assertThat(actual.getBody().getPageNumber()).isEqualTo(paginationResponse.getPageNumber());

    }

    @Test
    void shouldRetrieveCommentFromArchiveWhenStatusCodeIs2xx() throws JsonProcessingException {
        Long commentId = 1L;
        CommentResponse commentResponse = CommentResponseTestBuilder.aCommentResponse().build();
        String expected = objectMapper.writeValueAsString(commentResponse);

        stubFor(get(urlEqualTo("/comments/archive/" + commentId))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        ResponseEntity<CommentResponse> actual = commentService.getFromArchive(commentId).block();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getText()).isEqualTo("Test text comment");

    }

    @Test
    void shouldRetrieveAllPaginationResponseCommentsWhenStatusCodeIs2xx() throws JsonProcessingException {
        int pageSize = 1;
        int numberPage = 1;

        PaginationResponse<CommentResponse> paginationResponse = PaginationResponseForCommentTestBuilder
                .aPaginationResponse()
                .build();
        String expected = objectMapper.writeValueAsString(paginationResponse);

        stubFor(get(urlEqualTo("/comments" + "?pageSize=1&numberPage=1"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        ResponseEntity<PaginationResponse<CommentResponse>> actual = commentService.getAll(pageSize, numberPage).block();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getContent().size()).isOne();
        assertThat(actual.getBody().getCountPage()).isEqualTo(paginationResponse.getCountPage());
        assertThat(actual.getBody().getPageNumber()).isEqualTo(paginationResponse.getPageNumber());

    }

    @Test
    void shouldRetrieveAllPaginationResponseCommentsFromArchiveWhenStatusCodeIs2xx() throws JsonProcessingException {
        int pageSize = 1;
        int numberPage = 1;

        PaginationResponse<CommentResponse> paginationResponse = PaginationResponseForCommentTestBuilder
                .aPaginationResponse()
                .build();
        String expected = objectMapper.writeValueAsString(paginationResponse);

        stubFor(get(urlEqualTo("/comments/archive" + "?pageSize=1&numberPage=1"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        ResponseEntity<PaginationResponse<CommentResponse>> actual = commentService.getAllFromArchive(pageSize, numberPage).block();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getContent().size()).isOne();
        assertThat(actual.getBody().getCountPage()).isEqualTo(paginationResponse.getCountPage());
        assertThat(actual.getBody().getPageNumber()).isEqualTo(paginationResponse.getPageNumber());

    }

    @Test
    void shouldRetrieveCreatedCommentWhenStatusCodeIs2xx() throws JsonProcessingException {
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().build();

        when(httpServletRequest.getHeader("X-User-UUID")).thenReturn(userRequest.getUuid().toString());
        when(httpServletRequest.getHeader("X-User-Name")).thenReturn(userRequest.getUserName());
        CommentRequest request = CommentRequestTestBuilder.aCommentRequest().build();
        ModifyCommentRequest modifyCommentRequest = ModifyCommentRequest.builder()
                .text(request.text())
                .newsId(request.newsId())
                .user(userRequest)
                .build();
        String expected = objectMapper.writeValueAsString(modifyCommentRequest);

        stubFor(post(urlEqualTo("/comments"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        ResponseEntity<CommentResponse> actual = commentService.create(request, httpServletRequest).block();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getText()).isEqualTo("Test text comment");
        assertThat(actual.getBody().getUser().userName()).isEqualTo(userRequest.getUserName());
        assertThat(actual.getBody().getNewsId()).isEqualTo(request.newsId());

    }

    @Test
    void shouldRetrieveUpdatedCommentWhenStatusCodeIs2xx() throws JsonProcessingException {
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().build();
        long commentId = 1L;
        when(httpServletRequest.getHeader("X-User-UUID")).thenReturn(userRequest.getUuid().toString());
        when(httpServletRequest.getHeader("X-User-Name")).thenReturn(userRequest.getUserName());
        CommentRequest request = CommentRequestTestBuilder.aCommentRequest().build();
        ModifyCommentRequest modifyCommentRequest = ModifyCommentRequest.builder()
                .text(request.text())
                .newsId(request.newsId())
                .user(userRequest)
                .build();
        String expected = objectMapper.writeValueAsString(modifyCommentRequest);

        stubFor(get(urlEqualTo("/comments/" + commentId))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        stubFor(put(urlEqualTo("/comments/" + commentId))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        ResponseEntity<CommentResponse> actual = commentService.update(commentId, request, httpServletRequest).block();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getText()).isEqualTo("Test text comment");
        assertThat(actual.getBody().getUser().userName()).isEqualTo(userRequest.getUserName());
        assertThat(actual.getBody().getNewsId()).isEqualTo(request.newsId());

    }

    @Test
    void shouldThrowAccessDeniedExceptionInMethodUpdate() throws JsonProcessingException {
        long commentId = 1L;
        UserRequest userRequest = UserRequestBuilderTest.aUserRequest().build();
        when(httpServletRequest.getHeader("X-User-UUID")).thenReturn("93dee9c7-1756-4fe7-bd97-7b545c0e9467");
        when(httpServletRequest.getHeader("X-User-Name")).thenReturn(userRequest.getUserName());
        CommentRequest request = CommentRequestTestBuilder.aCommentRequest().build();
        ModifyCommentRequest modifyCommentRequest = ModifyCommentRequest.builder()
                .text(request.text())
                .newsId(request.newsId())
                .user(userRequest)
                .build();
        String expected = objectMapper.writeValueAsString(modifyCommentRequest);

        stubFor(get(urlEqualTo("/comments/" + commentId))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(expected)));

        StepVerifier.create(commentService.update(commentId, request, httpServletRequest))
                .expectError(AccessDeniedException.class)
                .verify();

    }
}