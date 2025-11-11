package dev.jjerome.qoq.test.app.common.library.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public final class AuthenticatedIdentityAccessUser implements IdentityAccessUser {
    private final String id;
    private final String username;
    private final String email;


    @Override
    public boolean authenticated() {
        return true;
    }

    @Override
    public boolean anonymous() {
        return false;
    }
}
