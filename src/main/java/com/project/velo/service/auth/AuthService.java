package com.project.velo.service.auth;


import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.dto.auth.AuthResponseDto;
import com.project.velo.dto.auth.LoginRequestDto;
import com.project.velo.entity.User;
import com.project.velo.entity.enums.Role;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.UserMapper;
import com.project.velo.repository.UserRepository;
import com.project.velo.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public ProfileResponseDto addUser(UserCreateDto dto) {
        User user = mapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        User savedUser = userRepository.save(user);

        return mapper.toProfileDto(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        var userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        String token = jwtUtil.generateToken(
                userDetails.getUsername(),
                roles);

        return new AuthResponseDto(token);
    }

    @Transactional
    public ProfileResponseDto addAdmin(UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new ValidationException("Пользователь с именем " + dto.username() + " уже существует");
        }

        User user = mapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        user.setRole(Role.ROLE_ADMIN);
        User savedUser = userRepository.save(user);

        return mapper.toProfileDto(savedUser);

    }
}
