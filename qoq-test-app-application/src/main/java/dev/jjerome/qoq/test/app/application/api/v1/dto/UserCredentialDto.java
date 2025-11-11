package dev.jjerome.qoq.test.app.application.api.v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCredentialDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
