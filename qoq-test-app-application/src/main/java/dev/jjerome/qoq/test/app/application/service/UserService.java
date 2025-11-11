package dev.jjerome.qoq.test.app.application.service;

import dev.jjerome.qoq.test.app.application.api.v1.dto.UserCreateDto;
import dev.jjerome.qoq.test.app.application.api.v1.dto.UserCredentialDto;
import dev.jjerome.qoq.test.app.application.api.v1.dto.UserSingInResponseDto;
import dev.jjerome.qoq.test.app.application.api.v1.dto.UserWhoamiDetailsDto;
import dev.jjerome.qoq.test.app.application.domain.User;
import dev.jjerome.qoq.test.app.application.exception.UserEmailAlreadyExistsException;
import dev.jjerome.qoq.test.app.application.exception.UserNotFoundException;
import dev.jjerome.qoq.test.app.application.exception.UserSignInException;
import dev.jjerome.qoq.test.app.application.mapper.UserMapper;
import dev.jjerome.qoq.test.app.application.repository.UserRepository;
import dev.jjerome.qoq.test.app.common.library.security.ApplicationUserResolver;
import dev.jjerome.qoq.test.app.common.library.security.IdentityAccessUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserRepository repository;
    private final ApplicationUserResolver userResolver;
    private final PasswordEncoder passwordEncoder;

    public UserSingInResponseDto signUp(UserCreateDto userCreateDto) {
        Optional<User> existingUser = repository.findByEmail(userCreateDto.getEmail());
        if (existingUser.isPresent()) {
            throw new UserEmailAlreadyExistsException("The user with this email already exists");
        }

        User entity = userMapper.asUser(userCreateDto);
        String hashedPassword = passwordEncoder.encode(userCreateDto.getPassword());
        entity.setPassword(hashedPassword);

        User savedUser = repository.insert(entity);
        UserSingInResponseDto responseDto = new UserSingInResponseDto();
        responseDto.setToken(jwtService.generateToken(userMapper.asIdentityAccessUser(savedUser)));

        return responseDto;
    }

    public UserSingInResponseDto signIn(UserCredentialDto credential) {
        User user = repository.findByEmail(credential.getEmail())
                .orElseThrow(() -> new UserSignInException("Invalid email or password"));

        if (!passwordEncoder.matches(credential.getPassword(), user.getPassword())) {
            throw new UserSignInException("Invalid email or password");
        }

        UserSingInResponseDto responseDto = new UserSingInResponseDto();
        responseDto.setToken(jwtService.generateToken(userMapper.asIdentityAccessUser(user)));

        return responseDto;
    }

    @Transactional(readOnly = true)
    public UserWhoamiDetailsDto whoami() {
        IdentityAccessUser currentUser = userResolver.resolveCurrent();
        User dbUser = repository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.asUserWhoamiDetailsDto(dbUser);
    }
}
