package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.entity.dto.CommentResponse;

import java.util.ArrayList;
import java.util.List;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aPaginationResponse")
public class PaginationResponseForCommentTestBuilder implements TestBuilder<PaginationResponse<CommentResponse>> {

    private int pageNumber = 1;
    private int countPage = 1;
    private List<CommentResponse> content = new ArrayList<>();

    @Override
    public PaginationResponse<CommentResponse> build() {
        content.add(CommentResponseTestBuilder.aCommentResponse().build());
        return new PaginationResponse<>(pageNumber, countPage, content);
    }
}
