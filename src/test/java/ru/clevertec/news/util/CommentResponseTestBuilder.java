package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.entity.dto.CommentResponse;
import ru.clevertec.news.entity.dto.UserResponse;

import java.time.LocalDateTime;


@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aCommentResponse")
public class CommentResponseTestBuilder implements TestBuilder<CommentResponse> {

    private Long id = 1L;
    private Long newsId = 1L;
    private String time = LocalDateTime.MIN.toString();
    private String text = "Test text comment";
    private UserResponse userResponse = UserResponseBuilderTest.aUserResponse().build();

    @Override
    public CommentResponse build() {
        return new CommentResponse(id, time, text, userResponse, newsId);
    }
}
