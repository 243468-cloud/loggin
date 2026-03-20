package com.example.loginapp.controller;

import com.example.loginapp.dto.LoginRequestDto;
import com.example.loginapp.dto.UserCreateDto;
import com.example.loginapp.dto.UserResponseDto;
import com.example.loginapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UserController — Endpoints REST para la gestión de usuarios.
 *
 *  GET    /api/users          → Listar todos
 *  GET    /api/users/{id}     → Buscar por ID
 *  POST   /api/users/register → Registrar nuevo usuario
 *  POST   /api/users/login    → Iniciar sesión
 *  DELETE /api/users/{id}     → Eliminar usuario
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    // ─── GET: Listar todos los usuarios ─────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // ─── GET: Obtener usuario por ID ─────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // ─── POST: Registrar nuevo usuario ───────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserCreateDto dto) {
        UserResponseDto created = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ─── POST: Login ─────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto dto) {
        try {
            UserResponseDto user = userService.login(dto);
            return ResponseEntity.ok(Map.of(
                    "message", "Login exitoso",
                    "user", user
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // ─── DELETE: Eliminar usuario por ID ─────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
    }
}
