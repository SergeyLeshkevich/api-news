package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.entity.dto.UserResponse;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aUserResponse")
public class UserResponseBuilderTest implements TestBuilder<UserResponse> {

    private Long id = 1L;
    private String userName = "Test userName";
    private String name = "Test name";

    @Override
    public UserResponse build() {
        return new UserResponse(userName);
    }
}
