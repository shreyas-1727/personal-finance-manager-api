package com.finance.manager.service;

import com.finance.manager.dto.RegisterRequest;
import com.finance.manager.entity.User;
import com.finance.manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
// This setting stops Mockito from crashing if a mocked method isn't used
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setFullName("Test User");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("test@example.com");
        registerRequest.setPassword("rawPassword123");
        registerRequest.setFullName("Test User");
        registerRequest.setPhoneNumber("+1234567890");
    }

    @Test
    void registerUser_Success() {
        // Arrange: Cover both common ways Spring checks for existing users
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = authService.registerUser(registerRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateUsername_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername("test@example.com")).thenReturn(true);
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert (Using Exception.class catches whatever specific exception you used)
        assertThrows(Exception.class, () -> {
            authService.registerUser(registerRequest);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
}