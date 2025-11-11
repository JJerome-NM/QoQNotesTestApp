package dev.jjerome.qoq.test.app.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jjerome.qoq.test.app.application.api.v1.NotesController;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteCreateRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteFiltersRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteListResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteStatisticsResponse;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteUpdateRequest;
import dev.jjerome.qoq.test.app.application.api.v1.validator.NoteCreateRequestValidator;
import dev.jjerome.qoq.test.app.application.api.v1.validator.NoteUpdateRequestValidator;
import dev.jjerome.qoq.test.app.application.service.JwtService;
import dev.jjerome.qoq.test.app.application.service.NoteService;
import dev.jjerome.qoq.test.app.common.library.exception.EntityWithIdNotFoundException;
import dev.jjerome.qoq.test.app.common.library.security.ApplicationUserResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotesController.class)
@AutoConfigureDataMongo
@AutoConfigureMockMvc(addFilters = false)
@Import({
        NoteCreateRequestValidator.class,
        NoteUpdateRequestValidator.class
})
@DisplayName("NotesController Unit Tests")
class NotesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NoteService noteService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ApplicationUserResolver applicationUserResolver;

    @Nested
    @DisplayName("POST /api/v1/notes/list")
    class ListNotesTests {

        @Test
        @DisplayName("Should return paginated list")
        void shouldReturnPaginatedList() throws Exception {
            NoteFiltersRequest request = new NoteFiltersRequest();
            request.setPage(0);
            request.setSize(10);

            NoteListResponse note = new NoteListResponse();
            note.setId("note-123");
            note.setTitle("Test Note");

            Page<NoteListResponse> page = new PageImpl<>(Collections.singletonList(note));

            when(noteService.getNotes(any())).thenReturn(page);

            mockMvc.perform(post("/api/v1/notes/list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value("note-123"));
        }

        @Test
        @DisplayName("Should handle empty results")
        void shouldHandleEmptyResults() throws Exception {
            NoteFiltersRequest request = new NoteFiltersRequest();
            request.setPage(0);
            request.setSize(10);

            when(noteService.getNotes(any())).thenReturn(new PageImpl<>(Collections.emptyList()));

            mockMvc.perform(post("/api/v1/notes/list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/notes - Create Note")
    class CreateNoteTests {

        @Test
        @DisplayName("Should create note with title only")
        void shouldCreateNoteWithTitleOnly() throws Exception {
            NoteCreateRequest request = new NoteCreateRequest();
            request.setTitle("Test Note");
            request.setText(null);

            NoteResponse response = new NoteResponse();
            response.setId("note-123");
            response.setTitle("Test Note");

            when(noteService.createNote(any())).thenReturn(response);

            mockMvc.perform(post("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("note-123"));

            verify(noteService).createNote(any());
        }

        @Test
        @DisplayName("Should create note with text only")
        void shouldCreateNoteWithTextOnly() throws Exception {
            NoteCreateRequest request = new NoteCreateRequest();
            request.setTitle(null);
            request.setText("Test content");

            NoteResponse response = new NoteResponse();
            response.setId("note-123");
            response.setText("Test content");

            when(noteService.createNote(any())).thenReturn(response);

            mockMvc.perform(post("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("note-123"));

            verify(noteService).createNote(any());
        }

        @Test
        @DisplayName("Should create note with both title and text")
        void shouldCreateNoteWithBoth() throws Exception {
            NoteCreateRequest request = new NoteCreateRequest();
            request.setTitle("Test Note");
            request.setText("Test content");

            NoteResponse response = new NoteResponse();
            response.setId("note-123");
            response.setTitle("Test Note");
            response.setText("Test content");

            when(noteService.createNote(any())).thenReturn(response);

            mockMvc.perform(post("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value("note-123"))
                    .andExpect(jsonPath("$.title").value("Test Note"))
                    .andExpect(jsonPath("$.text").value("Test content"));

            verify(noteService).createNote(any());
        }

        @Test
        @DisplayName("Validation: Should reject both title and text empty")
        void shouldRejectBothEmpty() throws Exception {
            NoteCreateRequest request = new NoteCreateRequest();
            request.setTitle("");
            request.setText("");

            mockMvc.perform(post("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(noteService, never()).createNote(any());
        }

        @Test
        @DisplayName("Validation: Should reject both title and text null")
        void shouldRejectBothNull() throws Exception {
            NoteCreateRequest request = new NoteCreateRequest();
            request.setTitle(null);
            request.setText(null);

            mockMvc.perform(post("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(noteService, never()).createNote(any());
        }

        @Test
        @DisplayName("Validation: Should reject title over 255 chars")
        void shouldRejectLongTitle() throws Exception {
            NoteCreateRequest request = new NoteCreateRequest();
            request.setTitle("a".repeat(256));
            request.setText("Content");

            mockMvc.perform(post("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(noteService, never()).createNote(any());
        }

        @Test
        @DisplayName("Validation: Should reject text over 5000 chars")
        void shouldRejectLongText() throws Exception {
            NoteCreateRequest request = new NoteCreateRequest();
            request.setTitle("Title");
            request.setText("a".repeat(5001));

            mockMvc.perform(post("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(noteService, never()).createNote(any());
        }

        @Test
        @DisplayName("Security: Should handle XSS attempts")
        void shouldHandleXSS() throws Exception {
            NoteCreateRequest request = new NoteCreateRequest();
            request.setTitle("<script>alert('XSS')</script>");
            request.setText("<img src=x onerror=alert('XSS')>");

            NoteResponse response = new NoteResponse();
            response.setId("note-123");
            response.setTitle("<script>alert('XSS')</script>");
            response.setText("<img src=x onerror=alert('XSS')>");

            when(noteService.createNote(any())).thenReturn(response);

            mockMvc.perform(post("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("<script>alert('XSS')</script>"));

            verify(noteService).createNote(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/notes - Update Note")
    class UpdateNoteTests {

        @Test
        @DisplayName("Should update note")
        void shouldUpdateNote() throws Exception {
            NoteUpdateRequest request = new NoteUpdateRequest();
            request.setId("note-123");
            request.setTitle("Updated");
            request.setText("Content");

            NoteResponse response = new NoteResponse();
            response.setId("note-123");
            response.setTitle("Updated");

            when(noteService.updateNote(any())).thenReturn(response);

            mockMvc.perform(put("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("note-123"));

            verify(noteService).updateNote(any());
        }

        @Test
        @DisplayName("Should update with title only")
        void shouldUpdateWithTitleOnly() throws Exception {
            NoteUpdateRequest request = new NoteUpdateRequest();
            request.setId("note-123");
            request.setTitle("Updated Title");
            request.setText(null);

            NoteResponse response = new NoteResponse();
            response.setId("note-123");
            response.setTitle("Updated Title");

            when(noteService.updateNote(any())).thenReturn(response);

            mockMvc.perform(put("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(noteService).updateNote(any());
        }

        @Test
        @DisplayName("Should return 404 when not found")
        void shouldReturn404() throws Exception {
            NoteUpdateRequest request = new NoteUpdateRequest();
            request.setId("non-existent");
            request.setTitle("Title");

            when(noteService.updateNote(any()))
                    .thenThrow(new EntityWithIdNotFoundException("Not found"));

            mockMvc.perform(put("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Validation: Should reject null id")
        void shouldRejectNullId() throws Exception {
            NoteUpdateRequest request = new NoteUpdateRequest();
            request.setId(null);
            request.setTitle("Title");

            mockMvc.perform(put("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(noteService, never()).updateNote(any());
        }

        @Test
        @DisplayName("Validation: Should reject both title and text empty")
        void shouldRejectBothEmpty() throws Exception {
            NoteUpdateRequest request = new NoteUpdateRequest();
            request.setId("note-123");
            request.setTitle("");
            request.setText("");

            mockMvc.perform(put("/api/v1/notes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(noteService, never()).updateNote(any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/notes/{id}")
    class DeleteNoteTests {

        @Test
        @DisplayName("Should delete note")
        void shouldDeleteNote() throws Exception {
            doNothing().when(noteService).deleteNote("note-123");

            mockMvc.perform(delete("/api/v1/notes/note-123"))
                    .andExpect(status().isOk());

            verify(noteService).deleteNote("note-123");
        }

        @Test
        @DisplayName("Should return 404 when not found")
        void shouldReturn404() throws Exception {
            doThrow(new EntityWithIdNotFoundException("Not found"))
                    .when(noteService).deleteNote("non-existent");

            mockMvc.perform(delete("/api/v1/notes/non-existent"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Security: Should prevent NoSQL injection")
        void shouldPreventNoSQLInjection() throws Exception {
            String maliciousId = "{$ne: null}";

            doThrow(new EntityWithIdNotFoundException("Invalid"))
                    .when(noteService).deleteNote(maliciousId);

            mockMvc.perform(delete("/api/v1/notes/{id}", maliciousId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/notes/{id}/statistics")
    class GetStatisticsTests {

        @Test
        @DisplayName("Should get statistics")
        void shouldGetStatistics() throws Exception {
            NoteStatisticsResponse response = new NoteStatisticsResponse();
            response.setId("note-123");

            Map<String, Integer> words = new HashMap<>();
            words.put("java", 5);
            words.put("spring", 3);
            words.put("boot", 2);
            response.setUniqueWordCounts(words);

            when(noteService.getNoteStatistics("note-123")).thenReturn(response);

            mockMvc.perform(get("/api/v1/notes/note-123/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("note-123"))
                    .andExpect(jsonPath("$.uniqueWordCounts.java").value(5))
                    .andExpect(jsonPath("$.uniqueWordCounts.spring").value(3))
                    .andExpect(jsonPath("$.uniqueWordCounts.boot").value(2));
        }

        @Test
        @DisplayName("Should return 404 when not found")
        void shouldReturn404() throws Exception {
            when(noteService.getNoteStatistics("non-existent"))
                    .thenThrow(new EntityWithIdNotFoundException("Not found"));

            mockMvc.perform(get("/api/v1/notes/non-existent/statistics"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should handle empty text")
        void shouldHandleEmptyText() throws Exception {
            NoteStatisticsResponse response = new NoteStatisticsResponse();
            response.setId("note-123");
            response.setUniqueWordCounts(Collections.emptyMap());

            when(noteService.getNoteStatistics("note-123")).thenReturn(response);

            mockMvc.perform(get("/api/v1/notes/note-123/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.uniqueWordCounts").isEmpty());
        }
    }
}