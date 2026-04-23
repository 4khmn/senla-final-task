package com.project.velo.controller.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.MessageCreateDto;
import com.project.velo.dto.response.chat.MessageResponseDto;
import com.project.velo.dto.update.MessageUpdateDto;
import com.project.velo.exception.GlobalExceptionHandler;
import com.project.velo.service.social.MessageService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MessageControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController)
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
    void updateMessage_ShouldReturnDto_Success() throws Exception {
        MessageUpdateDto messageUpdateDto = new MessageUpdateDto("content");
        Long messageId = 1L;
        MessageResponseDto updated = new MessageResponseDto(1L, "content", LocalDateTime.now(), "sender", true, true);
        given(messageService.editMessage(messageId, messageUpdateDto, "testUser")).willReturn(updated);

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("content"));
    }

    @Test
    void updateMessage_ShouldReturnBadRequest_WhenContentIsBlank() throws Exception {
        MessageUpdateDto messageUpdateDto = new MessageUpdateDto("");
        Long messageId = 1L;
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageUpdateDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deleteMessage_Success() throws Exception {
        Long messageId = 1L;

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());

        verify(messageService).deleteMessage(any(), any());
    }


    @Test
    void sendMessage_ShouldReturnDto_Success() throws Exception {
        MessageCreateDto dto = new MessageCreateDto("content");
        Long chatId = 1L;

        MessageResponseDto messageResponseDto = new MessageResponseDto(1L, "content", LocalDateTime.now(), "sender", true, true);
        given(messageService.sendMessage(chatId, dto, "testUser")).willReturn(messageResponseDto);

        mockMvc.perform(post("/api/messages/chat/{chatId}", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("content"));
    }

    @Test
    void sendMessage_ShouldReturnBadRequest_WhenContentIsBlank() throws Exception {
        MessageCreateDto dto = new MessageCreateDto("");
        Long chatId = 1L;
        mockMvc.perform(post("/api/messages/chat/{chatId}", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
