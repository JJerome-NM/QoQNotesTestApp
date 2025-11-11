package dev.jjerome.qoq.test.app.application.api.v1.validator;

import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteCreateRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class NoteCreateRequestValidator extends AbstractNoteValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return NoteCreateRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NoteCreateRequest request = (NoteCreateRequest) target;

        validateTitleAndTextNotEmpty(request.getTitle(), request.getText(), errors);
    }
}
