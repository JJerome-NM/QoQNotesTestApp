package dev.jjerome.qoq.test.app.common.library.configuration;

import dev.jjerome.qoq.test.app.common.library.security.ApplicationUserResolver;
import dev.jjerome.qoq.test.app.common.library.security.audit.ApplicationAuditorAware;
import dev.jjerome.qoq.test.app.common.library.security.audit.ZonedDateTimeAuditProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing(
        auditorAwareRef = "ApplicationAuditorAware",
        dateTimeProviderRef = "zonedDateTimeProvider"
)
public class MongoAuditConfiguration {

    @Bean("ApplicationAuditorAware")
    public AuditorAware<String> applicationAuditorAware(ApplicationUserResolver resolver) {
        return new ApplicationAuditorAware(resolver);
    }

    @Bean("zonedDateTimeProvider")
    public DateTimeProvider zonedDateTimeProvider() {
        return new ZonedDateTimeAuditProvider();
    }
}