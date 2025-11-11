package dev.jjerome.qoq.test.app.application.api.v1.dto;

import dev.jjerome.qoq.test.app.application.constant.NoteTag;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NoteFiltersRequest {
    private List<NoteTag> tags;
    @NotNull
    private Integer page = 0;
    @NotNull
    private Integer size = 10;
}
