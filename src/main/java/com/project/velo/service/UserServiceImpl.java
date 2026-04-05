package com.project.velo.service;

import com.project.velo.dto.UserResponseDto;
import com.project.velo.entity.User;
import com.project.velo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;


    @Override
    public void updateRating(Long userId) {

    }

    @Override
    public UserResponseDto getById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void deactivate(Long id) {

    }

    @Override
    public List<UserResponseDto> getAll() {
        return List.of();
    }

}
