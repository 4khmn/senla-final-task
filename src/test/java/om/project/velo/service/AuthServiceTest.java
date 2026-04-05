package om.project.velo.service;

import com.project.velo.dto.UserCreateDto;
import com.project.velo.dto.auth.AuthResponseDto;
import com.project.velo.dto.auth.LoginRequestDto;
import com.project.velo.entity.Profile;
import com.project.velo.entity.User;
import com.project.velo.mapper.UserMapper;
import com.project.velo.repository.UserRepository;
import com.project.velo.security.JwtUtil;
import com.project.velo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper mapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;


    @Test
    void addUser_WithProfile_Success() {
        UserCreateDto request = new UserCreateDto(
                "username", "password", "email@test.com", "Ivan", "Ivanov"
        );

        User userFromMapper = new User();
        Profile profileFromMapper = Profile.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();
        userFromMapper.setProfile(profileFromMapper);
        profileFromMapper.setUser(userFromMapper);

        User savedUser = new User();
        savedUser.setId(1L);

        given(mapper.toEntity(request)).willReturn(userFromMapper);
        given(passwordEncoder.encode(anyString())).willReturn("encoded_pass");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        authService.addUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertEquals("encoded_pass", capturedUser.getPassword());

        assertNotNull(capturedUser.getProfile(), "Профиль должен быть привязан к юзеру");
        assertEquals("Ivan", capturedUser.getProfile().getFirstName());
        assertEquals("Ivanov", capturedUser.getProfile().getLastName());
        assertEquals(capturedUser, capturedUser.getProfile().getUser(), "Связь должна быть двусторонней");
    }


    @Test
    void login_Success() {
        LoginRequestDto request = new LoginRequestDto("username", "password");
        String expectedToken = "mocked-jwt-token";

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        "username",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        Authentication authentication = mock(Authentication.class);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userDetails);

        given(jwtUtil.generateToken(eq("username"), eq("ROLE_USER"))).willReturn(expectedToken);

        AuthResponseDto response = authService.login(request);

        assertNotNull(response);
        assertEquals(expectedToken, response.token());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("username", captor.getValue().getPrincipal());
        assertEquals("password", captor.getValue().getCredentials());

        verify(jwtUtil).generateToken("username", "ROLE_USER");
    }


    @Test
    void login_BadCredentials_ThrowsException() {
        LoginRequestDto request = new LoginRequestDto("user", "wrong_pass");

        given(authenticationManager.authenticate(any()))
                .willThrow(new BadCredentialsException("Invalid username or password"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(request));

        verifyNoInteractions(jwtUtil);
    }
}
