package dev.jjerome.qoq.test.app.application.api.v1.dto;

import dev.jjerome.qoq.test.app.application.constant.NoteTag;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NoteCreateRequest {
    @Size(max = 255)
    private String title;
    @Size(max = 5000)
    private String text;
    private List<NoteTag> tags;
}
