package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.entity.dto.UserRequest;

import java.util.UUID;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "aUserRequest")
public class UserRequestBuilderTest implements TestBuilder<UserRequest> {

    private String userName = "Test userName";
    private UUID uuid = UUID.fromString("0bdc4d34-af90-4b42-bba6-f588323c87d7");

    @Override
    public UserRequest build() {
        return new UserRequest(uuid, userName);
    }
}
