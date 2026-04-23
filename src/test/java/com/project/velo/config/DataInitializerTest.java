package com.project.velo.config;

import com.project.velo.entity.enums.Role;
import com.project.velo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(dataInitializer, "adminUsername", "admin");
        org.springframework.test.util.ReflectionTestUtils.setField(dataInitializer, "adminPassword", "password");
        org.springframework.test.util.ReflectionTestUtils.setField(dataInitializer, "email", "admin@test.com");
    }

    @Test
    void run_ShouldCreateAdmin_WhenAdminDoesNotExist() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded_pass");

        dataInitializer.run();

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("admin") &&
                        user.getRole() == Role.ROLE_ADMIN &&
                        user.getPassword().equals("encoded_pass") &&
                        user.getProfile() != null
        ));
    }

    @Test
    void run_ShouldDoNothing_WhenAdminExists() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        dataInitializer.run();

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }
}
