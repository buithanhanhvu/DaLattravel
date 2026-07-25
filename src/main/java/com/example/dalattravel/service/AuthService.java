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

    public User processGoogleLogin(String credentialToken) {
        try {
            String[] parts = credentialToken.split("\\.");
            if (parts.length >= 2) {
                byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(parts[1]);
                String payloadJson = new String(decodedBytes, StandardCharsets.UTF_8);

                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(payloadJson);

                String email = node.path("email").asText();
                String name = node.path("name").asText();

                if (email != null && !email.isBlank()) {
                    Optional<User> existingUser = userRepository.findByEmail(email);
                    if (existingUser.isPresent()) {
                        return existingUser.get();
                    }

                    String username = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
                    if (userRepository.existsByUsername(username)) {
                        username = username + "_" + (System.currentTimeMillis() % 1000);
                    }

                    User newUser = User.builder()
                            .username(username)
                            .email(email)
                            .fullName(name != null && !name.isBlank() ? name : username)
                            .password(hashPassword("google_oauth_" + System.currentTimeMillis()))
                            .role("USER")
                            .build();

                    return userRepository.save(newUser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Không thể xác thực tài khoản Google!");
    }
}
