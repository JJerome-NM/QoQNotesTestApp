package dev.jjerome.qoq.test.app.common.library.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(EntityWithIdNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityWithIdNotFoundException ex) {
        log.error("Entity not found exception: {}", ex.getMessage());

        return ResponseEntity.notFound().build();
    }
}
