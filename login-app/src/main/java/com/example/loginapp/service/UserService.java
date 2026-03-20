package com.example.loginapp.service;

import com.example.loginapp.dto.LoginRequestDto;
import com.example.loginapp.dto.UserCreateDto;
import com.example.loginapp.dto.UserResponseDto;
import com.example.loginapp.model.User;
import com.example.loginapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── GET ALL ──────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        return toDto(user);
    }

    // ── POST: CREAR USUARIO ───────────────────────────────────────────────────
    @Transactional
    public UserResponseDto create(UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El username '" + dto.getUsername() + "' ya está en uso");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email '" + dto.getEmail() + "' ya está en uso");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(User.Role.USER)
                .active(true)
                .build();

        return toDto(userRepository.save(user));
    }

    // ── POST: LOGIN ───────────────────────────────────────────────────────────
    public UserResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        if (!user.isActive()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        return toDto(user);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }

    // ── MAPPER ────────────────────────────────────────────────────────────────
    private UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
