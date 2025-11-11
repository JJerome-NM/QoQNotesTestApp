package dev.jjerome.qoq.test.app.application.api.v1;

import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteCreateRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteFiltersRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteListResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteStatisticsResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteTextResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteUpdateRequest;
import dev.jjerome.qoq.test.app.application.api.v1.validator.NoteCreateRequestValidator;
import dev.jjerome.qoq.test.app.application.api.v1.validator.NoteUpdateRequestValidator;
import dev.jjerome.qoq.test.app.application.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NotesController {
    private final NoteService service;
    private final NoteCreateRequestValidator noteCreateRequestValidator;
    private final NoteUpdateRequestValidator noteUpdateRequestValidator;

    @InitBinder("noteCreateRequest")
    public void initCreateBinder(WebDataBinder binder) {
        binder.addValidators(noteCreateRequestValidator);
    }

    @InitBinder("noteUpdateRequest")
    public void initUpdateBinder(WebDataBinder binder) {
        binder.addValidators(noteUpdateRequestValidator);
    }

    @GetMapping("/{id}")
    public NoteTextResponse get(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping("/list")
    public Page<NoteListResponse> list(@Valid @RequestBody NoteFiltersRequest request) {
        return service.getNotes(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@Valid @RequestBody NoteCreateRequest noteCreateRequest) {
        return service.createNote(noteCreateRequest);
    }

    @PutMapping
    public NoteResponse update(@Valid @RequestBody NoteUpdateRequest noteUpdateRequest) {
        return service.updateNote(noteUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteNote(id);
    }

    @GetMapping("/{id}/statistics")
    public NoteStatisticsResponse getStatistics(@PathVariable String id) {
        return service.getNoteStatistics(id);
    }
}
