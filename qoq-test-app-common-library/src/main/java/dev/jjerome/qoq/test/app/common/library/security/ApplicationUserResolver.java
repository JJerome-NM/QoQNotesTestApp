package dev.jjerome.qoq.test.app.common.library.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ApplicationUserResolver {

    public IdentityAccessUser resolveCurrent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymousPrincipal(auth)) {
            return AnonymousIdentityAccessUser.get();
        }
        if (auth.getPrincipal() instanceof AuthenticatedIdentityAccessUser user) {
            return user;
        }

        return AnonymousIdentityAccessUser.get();
    }

    private boolean isAnonymousPrincipal(Authentication auth) {
        return Objects.isNull(auth) || Objects.isNull(auth.getPrincipal()) || !auth.isAuthenticated() || "anonymousUser".equals(String.valueOf(auth.getPrincipal()));
    }
}
