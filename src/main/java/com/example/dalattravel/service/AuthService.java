package com.example.dalattravel.service;

import com.example.dalattravel.model.User;
import com.example.dalattravel.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String hashPassword(String rawPassword) {
        if (rawPassword == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            return rawPassword;
        }
    }

    public User registerUser(String username, String email, String rawPassword, String fullName, String phoneNumber, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại trong hệ thống!");
        }
        if (email != null && !email.isBlank() && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(hashPassword(rawPassword))
                .fullName(fullName != null && !fullName.isBlank() ? fullName : username)
                .phoneNumber(phoneNumber)
                .role(role != null && !role.isBlank() ? role : "USER")
                .build();

        return userRepository.save(user);
    }

    public Optional<User> authenticate(String usernameOrEmail, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hashedInput = hashPassword(rawPassword);
            if (hashedInput.equalsIgnoreCase(user.getPassword()) || rawPassword.equals(user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
