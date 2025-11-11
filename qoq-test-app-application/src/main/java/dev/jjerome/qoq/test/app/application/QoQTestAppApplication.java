package dev.jjerome.qoq.test.app.application;

import dev.jjerome.qoq.test.app.common.library.AbstractApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QoQTestAppApplication extends AbstractApplication {
    public static void main(String[] args) {
        SpringApplication.run(QoQTestAppApplication.class, args);
    }
}
