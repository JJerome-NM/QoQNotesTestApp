package dev.jjerome.qoq.test.app.application.mapper;

import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteCreateRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteListResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteStatisticsResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteTextResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteUpdateRequest;
import dev.jjerome.qoq.test.app.application.domain.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note asNote(NoteCreateRequest request);

    NoteResponse asNoteResponse(Note note);

    NoteTextResponse asNoteTextResponse(Note note);

    NoteStatisticsResponse asNoteStatisticsResponse(Note note);

    NoteListResponse asNoteListResponse(Note note);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Note note, NoteUpdateRequest request);
}
