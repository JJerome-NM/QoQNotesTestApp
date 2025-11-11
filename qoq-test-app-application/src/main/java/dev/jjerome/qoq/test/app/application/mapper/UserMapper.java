package dev.jjerome.qoq.test.app.application.mapper;

import dev.jjerome.qoq.test.app.application.api.v1.dto.UserCreateDto;
import dev.jjerome.qoq.test.app.application.api.v1.dto.UserWhoamiDetailsDto;
import dev.jjerome.qoq.test.app.application.domain.User;
import dev.jjerome.qoq.test.app.common.library.security.AuthenticatedIdentityAccessUser;
import dev.jjerome.qoq.test.app.common.library.security.IdentityAccessUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User asUser(UserCreateDto userCreateDto);

    UserWhoamiDetailsDto asUserWhoamiDetailsDto(User user);

    default IdentityAccessUser asIdentityAccessUser(User user) {
        return new AuthenticatedIdentityAccessUser(user.getId(), user.getUsername(), user.getEmail());
    }
}
