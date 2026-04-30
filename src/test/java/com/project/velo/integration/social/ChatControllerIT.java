package com.project.velo.integration.social;

import com.project.velo.integration.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ChatControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/sql/social/init_chats.sql")
    void getOrCreateChat_ShouldReturnCreatedStatus() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/social/chat_create_response.json"));

        mockMvc.perform(post("/api/chats")
                        .param("adId", "500")
                        .with(user("buyer_user")))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/social/init_chats.sql")
    void getMyChats_ShouldReturnChatList() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/social/chat_list_response.json"));

        mockMvc.perform(get("/api/chats")
                        .with(user("buyer_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, false))
                .andExpect(jsonPath("$.content[0].updatedAt").exists());
    }

    @Test
    @Sql("/sql/social/init_chats.sql")
    void getChatMessages_ShouldReturnHistory() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/social/chat_messages_response.json"));

        // Проверяем историю от лица покупателя (isMine для его сообщения должно быть true)
        mockMvc.perform(get("/api/chats/1/messages")
                        .with(user("buyer_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/social/init_chats.sql")
    void getChatMessages_ShouldReturnNotFound_WhenChatDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/chats/999/messages")
                        .with(user("buyer_user")))
                .andExpect(status().isNotFound());
    }
}