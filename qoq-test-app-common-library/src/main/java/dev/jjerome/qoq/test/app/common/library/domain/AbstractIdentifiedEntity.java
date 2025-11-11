package dev.jjerome.qoq.test.app.common.library.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
public abstract class AbstractIdentifiedEntity {

    @Id
    @MongoId(targetType = FieldType.OBJECT_ID)
    @Field(name = "id")
    private String id;
}
