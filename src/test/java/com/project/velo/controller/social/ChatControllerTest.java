package com.project.velo.controller.social;

import com.project.velo.dto.response.chat.ChatListResponseDto;
import com.project.velo.dto.response.chat.ChatResponseDto;
import com.project.velo.dto.response.chat.MessageResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.exception.GlobalExceptionHandler;
import com.project.velo.service.social.ChatService;
import com.project.velo.service.social.MessageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChatService chatService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().isAssignableFrom(UserDetails.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return User.withUsername("testUser").password("pass").authorities("ROLE_USER").build();
                    }
                })
                .build();
    }

    @Test
    void getOrCreateChat_ShouldReturnDto_Success() throws Exception {
        Long adId = 1L;
        ChatResponseDto responseDto = new ChatResponseDto(1L, adId, "sellerUsername", "buyerUsername");

        given(chatService.getOrCreate(adId, "testUser")).willReturn(responseDto);

        mockMvc.perform(post("/api/chats")
                        .param("adId", adId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.advertisementId").value(adId));
    }

    @Test
    void getMyChats_ShouldReturnPageResponse_Success() throws Exception {
        ChatListResponseDto chatListItem = new ChatListResponseDto(
                1L, 1L, "title", "hello", LocalDateTime.now(), "user", "avatar");
        PageResponse<ChatListResponseDto> pageResponse = new PageResponse<>(List.of(chatListItem), 1, 1, 0, 15);

        given(chatService.findAllByUsername("testUser", 0, 15)).willReturn(pageResponse);

        mockMvc.perform(get("/api/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].lastMessageContent").value("hello"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getChatMessages_ShouldReturnPageResponse_Success() throws Exception {
        Long chatId = 1L;
        MessageResponseDto messageDto = new MessageResponseDto(
                10L, "content", LocalDateTime.now(), "testUser", true, false);
        PageResponse<MessageResponseDto> pageResponse = new PageResponse<>(List.of(messageDto), 1, 1, 0, 15);

        given(messageService.getMessagesByChat(chatId, "testUser", 0, 15)).willReturn(pageResponse);

        mockMvc.perform(get("/api/chats/{chatId}/messages", chatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("content"))
                .andExpect(jsonPath("$.content[0].senderUsername").value("testUser"));
    }

    @Test
    void getChatMessages_ShouldReturnNotFound_WhenChatDoesNotExist() throws Exception {
        Long chatId = 1L;

        given(messageService.getMessagesByChat(eq(chatId), eq("testUser"), anyInt(), anyInt()))
                .willThrow(new EntityNotFoundException("Чат с id " + chatId + " не найден"));

        mockMvc.perform(get("/api/chats/{chatId}/messages", chatId))
                .andExpect(status().isNotFound());
    }
}
