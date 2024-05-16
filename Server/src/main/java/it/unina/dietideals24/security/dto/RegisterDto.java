package it.unina.dietideals24.security.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String email;
    private String password;
    private String name;
    private String surname;
}
