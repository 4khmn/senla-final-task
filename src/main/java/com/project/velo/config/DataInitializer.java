package com.project.velo.config;

import com.project.velo.entity.Profile;
import com.project.velo.entity.User;
import com.project.velo.entity.enums.Role;
import com.project.velo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${app.init.admin.username}")
    private String adminUsername;

    @Value("${app.init.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!userRepository.existsByUsername(adminUsername)) {
            log.info("Инициализация базы данных: создание администратора '{}'...", adminUsername);

            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setEnabled(true);

            Profile adminProfile = new Profile();
            adminProfile.setUser(admin);
            admin.setProfile(adminProfile);

            userRepository.save(admin);

            log.info("Администратор успешно создан из настроек конфигурации.");
        } else {
            log.info("Пользователь '{}' уже существует, пропуск инициализации.", adminUsername);
        }
    }
}
