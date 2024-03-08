package ru.clevertec.news.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Builder
public record CommentRequest(

    @NotBlank
    @Size(min = 1, max = 500)
    String text,

    @NotNull
    Long newsId
){}

