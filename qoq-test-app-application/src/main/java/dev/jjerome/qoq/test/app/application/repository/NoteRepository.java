package dev.jjerome.qoq.test.app.application.repository;

import dev.jjerome.qoq.test.app.application.domain.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    Optional<Note> findByIdAndOwnerId(String id, String ownerId);
}
