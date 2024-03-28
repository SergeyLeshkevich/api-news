package ru.clevertec.news.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.clevertec.exceptionhandlerstarter.entity.IncorrectData;
import ru.clevertec.news.entity.dto.CommentRequest;
import ru.clevertec.news.entity.dto.CommentResponse;
import ru.clevertec.news.util.PaginationResponse;

import java.util.List;

@Validated
@RequestMapping(path = "/api/comments")
@Tag(name = "Comment service", description = "Operations related to comments")
public interface CommentController {

    @Operation(
            summary = "Get comment by ID",
            tags = {"Comment"},
            description = "Get comment. Returns a comment by ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comment"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("/{id}")
    Mono<ResponseEntity<CommentResponse>> getById(@PathVariable("id") Long id);

    @Operation(
            summary = "Get comment by ID related to news",
            tags = {"Comment"},
            description = "Get comment. Returns a comment by ID related to news.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comment"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("{id}/news/{idNews}")
    Mono<ResponseEntity<CommentResponse>> getByIdNews(
            @PathVariable("id") Long id,
            @PathVariable("idNews") Long idNews);

    @Operation(
            summary = "Get archived comment by ID",
            tags = {"Comment"},
            description = "Get comment. Returns a archived comment by ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comment"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("/archive/{id}")
    Mono<ResponseEntity<CommentResponse>> getFromArchive(@PathVariable("id") Long id);

    @GetMapping
    Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAll(
            @RequestParam(defaultValue = "15", name = "pageSize") int pageSize,
            @RequestParam(defaultValue = "1", name = "numberPage") int numberPage);

     @Operation(
            summary = "Get all comments related to news",
            tags = {"Comment"},
            description = "Returns successfully retrieved comment list related to news.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved comment list related to news",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("/news/{idNews}")
    Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAllByIdNews(
            @PathVariable(name = "idNews") Long idNews,
            @RequestParam(defaultValue = "15", name = "pageSize") int pageSize,
            @RequestParam(defaultValue = "1", name = "numberPage") int numberPage);

    @GetMapping("/archive")
    Mono<ResponseEntity<PaginationResponse<CommentResponse>>> getAllFromArchive(
            @RequestParam(defaultValue = "15", name = "pageSize") int pageSize,
            @RequestParam(defaultValue = "1", name = "numberPage") int numberPage);

    @Operation(
            summary = "Create new comment",
            tags = {"Comment"},
            description = "Comment creation. Returns the location of a new resource in headers.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Comment successfully created"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PostMapping
    Mono<ResponseEntity<CommentResponse>> create(@Valid @RequestBody CommentRequest commentDto, HttpServletRequest request);

    @Operation(
            summary = "Update comment by ID",
            tags = {"Comment"},
            description = "Comment update. Returns the updated resource.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Comment successfully updated"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body or input parameter",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PutMapping("/{id}")
    Mono<ResponseEntity<CommentResponse>> update(@PathVariable("id") Long id,
                                                 @Valid @RequestBody CommentRequest commentDto,
                                                 HttpServletRequest request);


    @Operation(
            summary = "Move comment to archive by ID",
            tags = {"Comment"},
            description = "Comment successfully moved to archive.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PatchMapping("/{id}")
    Mono<ResponseEntity<Void>> moveToArchive(@PathVariable("id") Long id, HttpServletRequest request);

    @Operation(
            summary = "Search for comments",
            tags = {"Comment"},
            description = "Successfully retrieved search results.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved search results",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("/search")
    Mono<ResponseEntity<List<CommentResponse>>> search(@RequestParam(name = "search") String searchValue,
                                                       @RequestParam(name = "offset") Integer offset,
                                                       @RequestParam(name = "limit") Integer limit);
}
