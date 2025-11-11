package dev.jjerome.qoq.test.app.application.api.v1.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class NoteListResponse {
    private String id;
    private String title;
    private Instant createdAt;
}
