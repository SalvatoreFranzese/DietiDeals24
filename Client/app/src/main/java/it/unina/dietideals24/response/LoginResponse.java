package it.unina.dietideals24.response;

import it.unina.dietideals24.model.DietiUser;

public class LoginResponse {
    private String token;
    private long expiresIn;
    private DietiUser dietiUser;

    public String getToken() {
        return token;
    }

    public DietiUser getDietiUser() {
        return dietiUser;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
