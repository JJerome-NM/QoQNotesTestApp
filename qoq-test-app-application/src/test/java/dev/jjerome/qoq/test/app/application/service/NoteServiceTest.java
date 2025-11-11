package dev.jjerome.qoq.test.app.application.service;

import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteCreateRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteFiltersRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteListResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteStatisticsResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteUpdateRequest;
import dev.jjerome.qoq.test.app.application.domain.Note;
import dev.jjerome.qoq.test.app.application.mapper.NoteMapper;
import dev.jjerome.qoq.test.app.application.repository.NoteRepository;
import dev.jjerome.qoq.test.app.application.repository.NoteSearchRepository;
import dev.jjerome.qoq.test.app.common.library.exception.EntityWithIdNotFoundException;
import dev.jjerome.qoq.test.app.common.library.security.ApplicationUserResolver;
import dev.jjerome.qoq.test.app.common.library.security.AuthenticatedIdentityAccessUser;
import dev.jjerome.qoq.test.app.common.library.security.IdentityAccessUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NoteService Unit Tests")
class NoteServiceTest {

    @Mock
    private NoteRepository repository;

    @Mock
    private NoteMapper mapper;

    @Mock
    private ApplicationUserResolver userResolver;

    @Mock
    private NoteSearchRepository searchRepository;

    @InjectMocks
    private NoteService noteService;

    private IdentityAccessUser currentUser;
    private Note note;
    private NoteResponse noteResponse;

    @BeforeEach
    void setUp() {
        currentUser = new AuthenticatedIdentityAccessUser("user-123", "testuser", "testUser@email.com");

        note = new Note();
        note.setId("note-123");
        note.setTitle("Test Note");
        note.setText("Test content with some test words");
        note.setOwnerId("user-123");

        noteResponse = new NoteResponse();
        noteResponse.setId("note-123");
        noteResponse.setTitle("Test Note");
        noteResponse.setText("Test content");
    }

    @Nested
    @DisplayName("getNotes() tests")
    class GetNotesTests {

        @Test
        @DisplayName("Should return paginated notes for current user")
        void shouldReturnPaginatedNotesForCurrentUser() {
            NoteFiltersRequest request = new NoteFiltersRequest();
            NoteListResponse listResponse = new NoteListResponse();
            Page<NoteListResponse> expectedPage = new PageImpl<>(Collections.singletonList(listResponse));

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(searchRepository.getNotes(eq("user-123"), eq(request))).thenReturn(expectedPage);

            Page<NoteListResponse> result = noteService.getNotes(request);

            assertThat(result).isEqualTo(expectedPage);
            verify(userResolver).resolveCurrent();
            verify(searchRepository).getNotes("user-123", request);
            verifyNoMoreInteractions(repository, mapper);
        }

        @Test
        @DisplayName("Security: Should only fetch notes for authenticated user")
        void shouldOnlyFetchNotesForAuthenticatedUser() {
            NoteFiltersRequest request = new NoteFiltersRequest();
            String userId = "user-123";

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(searchRepository.getNotes(anyString(), any())).thenReturn(Page.empty());

            noteService.getNotes(request);

            verify(searchRepository).getNotes(eq(userId), any());
            verify(searchRepository, never()).getNotes(argThat(id -> !id.equals(userId)), any());
        }
    }

    @Nested
    @DisplayName("createNote() tests")
    class CreateNoteTests {

        @Test
        @DisplayName("Should create note with current user as owner")
        void shouldCreateNoteWithCurrentUserAsOwner() {
            NoteCreateRequest request = new NoteCreateRequest();
            request.setTitle("New Note");
            request.setText("New content");

            Note newNote = new Note();
            newNote.setTitle("New Note");
            newNote.setText("New content");

            Note savedNote = new Note();
            savedNote.setId("note-123");
            savedNote.setTitle("New Note");
            savedNote.setText("New content");
            savedNote.setOwnerId("user-123");

            NoteResponse response = new NoteResponse();
            response.setId("note-123");
            response.setTitle("New Note");
            response.setText("New content");

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(mapper.asNote(request)).thenReturn(newNote);
            when(repository.insert(any(Note.class))).thenReturn(savedNote);
            when(mapper.asNoteResponse(any(Note.class))).thenReturn(response);

            NoteResponse result = noteService.createNote(request);

            assertThat(result).isEqualTo(response);

            verify(repository).insert(argThat((Note n) ->
                    Objects.nonNull(n) && "user-123".equals(n.getOwnerId())
            ));
        }

        @Test
        @DisplayName("Security: Should prevent creating note without authentication")
        void shouldPreventCreatingNoteWithoutAuthentication() {
            NoteCreateRequest request = new NoteCreateRequest();
            when(userResolver.resolveCurrent()).thenThrow(new SecurityException("User not authenticated"));

            assertThatThrownBy(() -> noteService.createNote(request))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("User not authenticated");

            verify(repository, never()).insert(any(Note.class));
        }
    }

    @Nested
    @DisplayName("updateNote() tests")
    class UpdateNoteTests {

        @Test
        @DisplayName("Should update note when user is owner")
        void shouldUpdateNoteWhenUserIsOwner() {
            NoteUpdateRequest request = new NoteUpdateRequest();
            request.setId("note-123");
            request.setTitle("Updated Title");
            request.setText("Updated content");

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(repository.findByIdAndOwnerId("note-123", "user-123"))
                    .thenReturn(Optional.of(note));
            when(repository.save(note)).thenReturn(note);
            when(mapper.asNoteResponse(note)).thenReturn(noteResponse);

            NoteResponse result = noteService.updateNote(request);

            assertThat(result).isEqualTo(noteResponse);
            verify(mapper).update(note, request);
            verify(repository).save(note);
        }

        @Test
        @DisplayName("Security: Should throw exception when updating note of another user")
        void shouldThrowExceptionWhenUpdatingNoteOfAnotherUser() {
            NoteUpdateRequest request = new NoteUpdateRequest();
            request.setId("note-123");
            request.setTitle("Malicious Update");

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(repository.findByIdAndOwnerId("note-123", "user-123"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.updateNote(request))
                    .isInstanceOf(EntityWithIdNotFoundException.class)
                    .hasMessageContaining("Note not found with id: note-123");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when note does not exist")
        void shouldThrowExceptionWhenNoteDoesNotExist() {
            NoteUpdateRequest request = new NoteUpdateRequest();
            request.setId("non-existent");

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(repository.findByIdAndOwnerId("non-existent", "user-123"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.updateNote(request))
                    .isInstanceOf(EntityWithIdNotFoundException.class);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteNote() tests")
    class DeleteNoteTests {

        @Test
        @DisplayName("Should delete note when user is owner")
        void shouldDeleteNoteWhenUserIsOwner() {
            String noteId = "note-123";

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(repository.findByIdAndOwnerId(noteId, "user-123"))
                    .thenReturn(Optional.of(note));

            noteService.deleteNote(noteId);

            verify(repository).delete(note);
        }

        @Test
        @DisplayName("Security: Should throw exception when deleting note of another user")
        void shouldThrowExceptionWhenDeletingNoteOfAnotherUser() {
            String noteId = "note-123";

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(repository.findByIdAndOwnerId(noteId, "user-123"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.deleteNote(noteId))
                    .isInstanceOf(EntityWithIdNotFoundException.class);

            verify(repository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("getNoteStatistics() tests")
    class GetNoteStatisticsTests {

        @Test
        @DisplayName("Should return statistics for user's note")
        void shouldReturnStatisticsForUsersNote() {
            String noteId = "note-123";

            NoteStatisticsResponse statsResponse = new NoteStatisticsResponse();
            statsResponse.setId(noteId);

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(repository.findByIdAndOwnerId(noteId, "user-123"))
                    .thenReturn(Optional.of(note));
            when(mapper.asNoteStatisticsResponse(note)).thenReturn(statsResponse);

            NoteStatisticsResponse result = noteService.getNoteStatistics(noteId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(noteId);
            verify(userResolver).resolveCurrent();
            verify(repository).findByIdAndOwnerId(noteId, "user-123");
            verify(mapper).asNoteStatisticsResponse(note);
        }

        @Test
        @DisplayName("Security: Should not expose statistics of other users' notes")
        void shouldNotExposeStatisticsOfOtherUsersNotes() {
            String noteId = "note-123";

            when(userResolver.resolveCurrent()).thenReturn(currentUser);
            when(repository.findByIdAndOwnerId(noteId, "user-123"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.getNoteStatistics(noteId))
                    .isInstanceOf(EntityWithIdNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Security Tests - Authorization")
    class SecurityTests {

        @Test
        @DisplayName("Should always verify user ownership before operations")
        void shouldAlwaysVerifyUserOwnership() {
            String noteId = "note-123";
            IdentityAccessUser maliciousUser = new AuthenticatedIdentityAccessUser(
                    "malicious-user",
                    "hacker",
                    "best-hacker-in-the-world@gmail.com"
            );

            when(userResolver.resolveCurrent()).thenReturn(maliciousUser);
            when(repository.findByIdAndOwnerId(noteId, "malicious-user"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.deleteNote(noteId))
                    .isInstanceOf(EntityWithIdNotFoundException.class);

            verify(repository, never()).findById(any());
            verify(repository).findByIdAndOwnerId(noteId, "malicious-user");
        }
    }
}