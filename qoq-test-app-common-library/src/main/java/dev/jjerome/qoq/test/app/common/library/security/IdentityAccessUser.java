package dev.jjerome.qoq.test.app.common.library.security;

public interface IdentityAccessUser {
    String getId();

    String getUsername();

    String getEmail();

    boolean authenticated();

    boolean anonymous();
}
