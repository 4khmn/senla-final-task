package com.project.velo.service;


import com.project.velo.dto.UserCreateDto;
import com.project.velo.dto.UserResponseDto;
import com.project.velo.entity.Profile;
import com.project.velo.entity.User;
import com.project.velo.entity.enums.Role;
import com.project.velo.mapper.UserMapper;
import com.project.velo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper mapper;

    @Transactional
    public UserResponseDto addUser(UserCreateDto dto){
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(Role.ROLE_USER);
        user.setRating(BigDecimal.ZERO);
        user.setEnabled(true);

        Profile profile = Profile.builder()
                .user(user)
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .build();

        user.setProfile(profile);
        User savedUser = userRepository.save(user);

        return mapper.toDto(savedUser);
    }
}
