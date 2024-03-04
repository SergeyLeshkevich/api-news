package ru.clevertec.news.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsRequest {

    @NotBlank
    @Size(min = 1, max = 50)
  private  String title;

    @NotBlank
    @Size(min = 1, max = 2000)
   private String text;

    @NotNull
    UserRequest user;
}
