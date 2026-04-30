package com.project.velo.integration.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.MessageCreateDto;
import com.project.velo.dto.update.MessageUpdateDto;
import com.project.velo.integration.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MessageControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Sql("/sql/social/init_messages.sql")
    void sendMessage_ShouldReturnCreated() throws Exception {
        MessageCreateDto dto = new MessageCreateDto("content");

        mockMvc.perform(post("/api/messages/chat/1")
                        .with(user("sender_user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{'content': 'content', 'senderUsername': 'sender_user', 'isMine': true}", false));
    }

    @Test
    @Sql("/sql/social/init_messages.sql")
    void updateMessage_ShouldReturnUpdatedMessage() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/social/message_update_response.json"));
        MessageUpdateDto updateDto = new MessageUpdateDto("new content");

        mockMvc.perform(patch("/api/messages/100")
                        .with(user("sender_user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/social/init_messages.sql")
    void deleteMessage_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/messages/100")
                        .with(user("sender_user")))
                .andExpect(status().isNoContent());

        // Проверяем, что сообщения больше нет в чате
        mockMvc.perform(get("/api/chats/1/messages")
                        .with(user("sender_user")))
                .andExpect(status().isOk())
                .andExpect(content().json("{'totalElements': 0}", false));
    }

    @Test
    @Sql("/sql/social/init_messages.sql")
    void updateMessage_ShouldReturnForbidden_WhenNotAuthor() throws Exception {
        MessageUpdateDto updateDto = new MessageUpdateDto("hacker content");

        mockMvc.perform(patch("/api/messages/100")
                        .with(user("receiver_user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql("/sql/social/init_messages.sql")
    void deleteMessage_ShouldReturnForbidden_WhenNotAuthor() throws Exception {
        mockMvc.perform(delete("/api/messages/100")
                        .with(user("receiver_user")))
                .andExpect(status().isForbidden());
    }
}