package dev.jjerome.qoq.test.app.common.library.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
public abstract class AbstractAuditedEntity extends AbstractIdentifiedEntity {
    @CreatedBy
    @Field("createdBy")
    private String createdBy;

    @CreatedDate
    @Field("createdAt")
    private Instant createdAt;

    @LastModifiedBy
    @Field("updatedBy")
    private String updatedBy;

    @LastModifiedDate
    @Field("updatedAt")
    private Instant updatedAt;
}
