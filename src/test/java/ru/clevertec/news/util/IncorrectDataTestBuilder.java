package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.exceptionhandlerstarter.entity.IncorrectData;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "anIncorrectData")
public class IncorrectDataTestBuilder implements TestBuilder<IncorrectData> {

  private String exception="EntityNotFoundException";
  private String errorMessage="Entity with 1 not found";
  private String errorCode="404";
    @Override
    public IncorrectData build() {
        return new IncorrectData(exception,errorMessage,errorCode);
    }
}
