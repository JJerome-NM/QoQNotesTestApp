package dev.jjerome.qoq.test.app.application.api.v1.validator;

import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class NoteUpdateRequestValidator extends AbstractNoteValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return NoteUpdateRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NoteUpdateRequest request = (NoteUpdateRequest) target;

        validateTitleAndTextNotEmpty(request.getTitle(), request.getText(), errors);
    }
}
