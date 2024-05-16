package it.unina.dietideals24.dto;

public class LoginDto {
    private final String email;
    private final String password;

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
