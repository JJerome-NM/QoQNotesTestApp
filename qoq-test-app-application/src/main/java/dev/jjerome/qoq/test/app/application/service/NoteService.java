package dev.jjerome.qoq.test.app.application.service;

import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteCreateRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteFiltersRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteListResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteStatisticsResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteTextResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteUpdateRequest;
import dev.jjerome.qoq.test.app.application.domain.Note;
import dev.jjerome.qoq.test.app.application.mapper.NoteMapper;
import dev.jjerome.qoq.test.app.application.repository.NoteRepository;
import dev.jjerome.qoq.test.app.application.repository.NoteSearchRepository;
import dev.jjerome.qoq.test.app.application.utils.NoteStatisticsUtils;
import dev.jjerome.qoq.test.app.common.library.exception.EntityWithIdNotFoundException;
import dev.jjerome.qoq.test.app.common.library.security.ApplicationUserResolver;
import dev.jjerome.qoq.test.app.common.library.security.IdentityAccessUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {
    private final NoteRepository repository;
    private final NoteMapper mapper;
    private final ApplicationUserResolver userResolver;
    private final NoteSearchRepository searchRepository;

    @Transactional(readOnly = true)
    public Page<NoteListResponse> getNotes(NoteFiltersRequest request) {
        IdentityAccessUser currentUser = userResolver.resolveCurrent();

        return searchRepository.getNotes(currentUser.getId(), request);
    }

    @Transactional(readOnly = true)
    public NoteTextResponse getById(String id) {
        return mapper.asNoteTextResponse(getByIdForCurrentUser(id));
    }

    public NoteResponse createNote(NoteCreateRequest request) {
        return mapper.asNoteResponse(create(request));
    }

    private Note create(NoteCreateRequest request) {
        Note note = mapper.asNote(request);
        note.setOwnerId(userResolver.resolveCurrent().getId());
        return repository.insert(note);
    }

    public NoteResponse updateNote(NoteUpdateRequest request) {
        return mapper.asNoteResponse(update(request));
    }

    private Note update(NoteUpdateRequest request) {
        Note note = getByIdForCurrentUser(request.getId());
        mapper.update(note, request);
        return repository.save(note);
    }

    public void deleteNote(String id) {
        Note note = getByIdForCurrentUser(id);
        repository.delete(note);
    }

    public NoteStatisticsResponse getNoteStatistics(String noteId) {
        Note note = getByIdForCurrentUser(noteId);
        NoteStatisticsResponse response = mapper.asNoteStatisticsResponse(note);
        Map<String, Integer> wordFrequencies = NoteStatisticsUtils.calculateWordFrequencies(note.getText());
        response.setUniqueWordCounts(wordFrequencies);
        return response;
    }


    private Note getByIdForCurrentUser(String noteId) {
        IdentityAccessUser currentUser = userResolver.resolveCurrent();
        return repository.findByIdAndOwnerId(noteId, currentUser.getId())
                .orElseThrow(() -> new EntityWithIdNotFoundException("Note not found with id: %s".formatted(noteId)));
    }
}
