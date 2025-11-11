package dev.jjerome.qoq.test.app.application.api.v1.dto;

import dev.jjerome.qoq.test.app.application.constant.NoteTag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NoteResponse {
    private String id;
    private String title;
    private String text;
    private List<NoteTag> tags;
}
