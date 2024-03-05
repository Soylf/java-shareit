package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void serializeTest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Name")
                .email("Email@gmail.com")
                .build();

        JsonContent<UserDto> write = json.write(userDto);

        assertThat(write).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(write).extractingJsonPathStringValue("$.name").isEqualTo("Name");
        assertThat(write).extractingJsonPathStringValue("$.email").isEqualTo("Email@gmail.com");
    }

    @Test
    @SneakyThrows
    void deserializeTest() {
        String jsonContent = "{\"id\":1,\"name\":\"Name\",\"email\":\"Email@gmail.com\"}";
        UserDto testObject = json.parse(jsonContent).getObject();

        assertThat(testObject.getId()).isEqualTo(1L);
        assertThat(testObject.getName()).isEqualTo("Name");
        assertThat(testObject.getEmail()).isEqualTo("Email@gmail.com");
    }
}
