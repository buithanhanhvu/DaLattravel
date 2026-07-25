package com.example.dalattravel;

import com.example.dalattravel.model.User;
import com.example.dalattravel.repository.UserRepository;
import com.example.dalattravel.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_UNIT_001: Đăng ký người dùng thành công")
    void testRegisterUser_Success() {
        when(userRepository.existsByUsername("qa_user")).thenReturn(false);
        when(userRepository.existsByEmail("qa@gmail.com")).thenReturn(false);

        User savedUser = User.builder()
                .id("1")
                .username("qa_user")
                .email("qa@gmail.com")
                .password(authService.hashPassword("123456"))
                .fullName("QA User")
                .role("USER")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.registerUser("qa_user", "qa@gmail.com", "123456", "QA User", "0901234567", "USER");

        assertNotNull(result);
        assertEquals("qa_user", result.getUsername());
        assertEquals("USER", result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("TC_UNIT_002: Đăng ký trùng Username ném ra ngoại lệ")
    void testRegisterUser_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser("existing_user", "new@gmail.com", "123456", "Name", "0901234567", "USER");
        });

        assertTrue(exception.getMessage().contains("Tên đăng nhập đã tồn tại"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("TC_UNIT_003: Đăng nhập thành công với mật khẩu chính xác")
    void testAuthenticate_ValidPassword_Success() {
        String hashedPassword = authService.hashPassword("password123");
        User mockUser = User.builder()
                .username("test_user")
                .password(hashedPassword)
                .fullName("Test User")
                .role("USER")
                .build();

        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(mockUser));

        Optional<User> authResult = authService.authenticate("test_user", "password123");

        assertTrue(authResult.isPresent());
        assertEquals("Test User", authResult.get().getFullName());
    }

    @Test
    @DisplayName("TC_UNIT_004: Đăng nhập thất bại khi sai mật khẩu")
    void testAuthenticate_InvalidPassword_Empty() {
        String hashedPassword = authService.hashPassword("correct_pass");
        User mockUser = User.builder()
                .username("test_user")
                .password(hashedPassword)
                .build();

        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(mockUser));

        Optional<User> authResult = authService.authenticate("test_user", "wrong_pass");

        assertFalse(authResult.isPresent());
    }
}
