package dev.jjerome.qoq.test.app.common.library.exception;

public class EntityWithIdNotFoundException extends RuntimeException {
    public EntityWithIdNotFoundException(String message) {
        super(message);
    }
}
