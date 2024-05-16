package it.unina.dietideals24.security.response;

import it.unina.dietideals24.model.DietiUser;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private long expiresIn;
    private DietiUser dietiUser;
}