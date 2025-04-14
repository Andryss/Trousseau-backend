package ru.andryss.trousseau.controller;

import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.andryss.trousseau.security.UserData;

public abstract class BaseApiController {

    @Nullable
    protected String getSession() {
        return getAuthentication()
                .map(auth -> ((String) auth.getCredentials()))
                .orElse(null);
    }

    @Nullable
    protected UserData getUser() {
        return getAuthentication()
                .map(auth -> ((UserData) auth.getPrincipal()))
                .orElse(null);
    }

    private Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

}
