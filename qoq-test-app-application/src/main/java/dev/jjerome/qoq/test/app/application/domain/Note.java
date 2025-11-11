package dev.jjerome.qoq.test.app.application.domain;


import dev.jjerome.qoq.test.app.application.constant.NoteTag;
import dev.jjerome.qoq.test.app.common.library.domain.AbstractAuditedEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@Document(collection = "notes")
public class Note extends AbstractAuditedEntity {
    @Field("title")
    @Indexed
    private String title;

    @Field("text")
    private String text;

    @Field("tags")
    private List<NoteTag> tags;

    @Field("ownerId")
    @Indexed
    private String ownerId;
}
