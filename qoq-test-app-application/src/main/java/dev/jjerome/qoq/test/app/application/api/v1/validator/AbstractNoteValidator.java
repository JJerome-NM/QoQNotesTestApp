package dev.jjerome.qoq.test.app.application.api.v1.validator;


import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public abstract class AbstractNoteValidator implements Validator {

    protected void validateTitleAndTextNotEmpty(String title, String text, Errors errors) {
        if (StringUtils.isAllBlank(title, text)) {
            errors.reject("validation.note.empty",
                    "Note title and text cannot be both empty.");
        }
    }
}
