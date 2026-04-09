package com.project.velo.service.user;

import com.project.velo.dto.response.ProfileResponseDto;
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
    public ProfileResponseDto getById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void deactivate(Long id) {

    }

    @Override
    public List<ProfileResponseDto> getAll() {
        return List.of();
    }

}
