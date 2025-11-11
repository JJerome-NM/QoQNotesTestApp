package dev.jjerome.qoq.test.app.application.repository;

import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteFiltersRequest;
import dev.jjerome.qoq.test.app.application.api.v1.dto.NoteListResponse;
import dev.jjerome.qoq.test.app.application.domain.Note;
import dev.jjerome.qoq.test.app.application.mapper.NoteMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoteSearchRepository {
    private static final int MAX_SIZE = 100;

    private final MongoTemplate mongoTemplate;
    private final NoteMapper noteMapper;

    public Page<NoteListResponse> getNotes(String ownerId, NoteFiltersRequest request) {
        int page = request.getPage();
        int sizeRaw = request.getSize();
        int size = Math.min(sizeRaw, MAX_SIZE);

        Criteria criteria = Criteria.where("ownerId").is(ownerId);
        if (CollectionUtils.isNotEmpty(request.getTags())) {
            criteria = criteria.and("tags").is(request.getTags());
        }

        Query baseQuery = new Query(criteria);
        baseQuery.fields()
                .include("title")
                .include("createdAt");

        long total = mongoTemplate.count(baseQuery, Note.class);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Query paged = baseQuery.with(pageable);

        List<Note> notes = mongoTemplate.find(paged, Note.class);

        List<NoteListResponse> content = notes.stream()
                .map(noteMapper::asNoteListResponse)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}

