package com.project.velo.controller;

import com.project.velo.dto.UserResponseDto;
import com.project.velo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;


    @GetMapping
    public List<UserResponseDto> getUsers() {
        log.info("GET /api/users - fetching all users");
        List<UserResponseDto> users = userService.getAll();
        log.info("GET /api/users - users successfully retrieved");
        return users;
    }

}
