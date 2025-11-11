package dev.jjerome.qoq.test.app.common.library;

import dev.jjerome.qoq.test.app.common.library.configuration.CommonConfiguration;
import org.springframework.context.annotation.Import;

@Import({CommonConfiguration.class})
public abstract class AbstractApplication {
}
