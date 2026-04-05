package com.project.velo.service;

import com.project.velo.entity.User;
import com.project.velo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public void register(User user) {

    }

    @Override
    public void updateRating(Long userId) {

    }

    public void a(){}
}
