package dev.jjerome.qoq.test.app.common.library.security.audit;

import dev.jjerome.qoq.test.app.common.library.security.ApplicationUserResolver;
import dev.jjerome.qoq.test.app.common.library.security.IdentityAccessUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@RequiredArgsConstructor
public class ApplicationAuditorAware implements AuditorAware<String> {

    private final ApplicationUserResolver userResolver;


    @Override
    public Optional<String> getCurrentAuditor() {
        IdentityAccessUser user = userResolver.resolveCurrent();

        if (user.authenticated()) {
            return Optional.ofNullable(user.getId());
        }

        return Optional.of("anonymousUser");
    }
}
