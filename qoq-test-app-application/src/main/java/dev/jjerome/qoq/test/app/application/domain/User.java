package dev.jjerome.qoq.test.app.application.domain;

import dev.jjerome.qoq.test.app.common.library.domain.AbstractAuditedEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "users")
public class User extends AbstractAuditedEntity {
    @Field("username")
    private String username;
    @Field("email")
    @Indexed
    private String email;
    @Field("password_hash")
    private String password;
}
