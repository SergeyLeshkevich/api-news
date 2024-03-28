package ru.clevertec.news.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.news.entity.dto.User;

import java.util.UUID;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "anUser")
public class UserTestBuilderTest implements TestBuilder<User> {

    private Long id = 1L;
    private UUID uuid = UUID.fromString("0bdc4d34-af90-4b42-bba6-f588323c87d7");
    private String userName = "Test userName";

    @Override
    public User build() {
        return new User(id, uuid, userName);
    }
}
