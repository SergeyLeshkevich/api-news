package ru.clevertec.news.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.clevertec.news.util.PaginationResponse;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewsResponse {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime time;
    private String title;
    private String text;
    private UserResponse user;
    private PaginationResponse<CommentResponse> comments;
}