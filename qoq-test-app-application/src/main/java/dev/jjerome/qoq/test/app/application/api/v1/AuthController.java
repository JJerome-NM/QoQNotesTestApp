package dev.jjerome.qoq.test.app.application.api.v1;

import dev.jjerome.qoq.test.app.application.api.v1.dto.UserCreateDto;
import dev.jjerome.qoq.test.app.application.api.v1.dto.UserCredentialDto;
import dev.jjerome.qoq.test.app.application.api.v1.dto.UserSingInResponseDto;
import dev.jjerome.qoq.test.app.application.api.v1.dto.UserWhoamiDetailsDto;
import dev.jjerome.qoq.test.app.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService service;

    @PostMapping("/signUp")
    public UserSingInResponseDto createUser(@RequestBody @Valid UserCreateDto createDto) {
        return service.signUp(createDto);
    }

    @PostMapping("/signIn")
    public UserSingInResponseDto signIn(@RequestBody @Valid UserCredentialDto credential) {
        return service.signIn(credential);
    }

    @GetMapping("/whoami")
    public UserWhoamiDetailsDto whoami() {
        return service.whoami();
    }
}
