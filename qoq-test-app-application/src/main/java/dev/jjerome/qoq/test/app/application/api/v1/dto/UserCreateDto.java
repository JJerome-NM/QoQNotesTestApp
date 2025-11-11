package dev.jjerome.qoq.test.app.application.api.v1.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    @NotBlank
    @Size(max = 35)
    private String username;
    @NotBlank
    @Size(max = 60)
    private String password;
    @NotBlank
    @Email
    @Size(max = 60)
    private String email;
}
