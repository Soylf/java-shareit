package ru.practicum.shareit.comment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentResponseDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    JacksonTester<CommentResponseDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .text("text")
                .build();

        JsonContent<CommentResponseDto> write = json.write(commentResponseDto);

        assertThat(write).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"text\":\"text\"}";
        CommentResponseDto object = json.parse(jsonContent).getObject();

        assertThat("text").isEqualTo(object.getText());
    }
}