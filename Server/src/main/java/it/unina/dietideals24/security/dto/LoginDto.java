package it.unina.dietideals24.security.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
}
