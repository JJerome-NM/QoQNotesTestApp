package dev.jjerome.qoq.test.app.common.library.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnonymousIdentityAccessUser implements IdentityAccessUser {
    private static final AnonymousIdentityAccessUser INSTANCE = new AnonymousIdentityAccessUser();

    public static AnonymousIdentityAccessUser get() {
        return INSTANCE;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public boolean authenticated() {
        return false;
    }

    @Override
    public boolean anonymous() {
        return true;
    }
}
