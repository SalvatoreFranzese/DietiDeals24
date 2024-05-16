package it.unina.dietideals24.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdatePasswordDto {
    private String oldPassword;
    private String newPassword;
}
