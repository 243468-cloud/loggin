package com.example.loginapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
}
