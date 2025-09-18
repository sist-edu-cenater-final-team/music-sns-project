package com.github.musicsnsproject.config.web.principal;

import lombok.AllArgsConstructor;

import java.security.Principal;
@AllArgsConstructor(staticName = "of")
public class StompPrincipal implements Principal {
    private final String userId;


    @Override
    public String getName() {
        return userId;
    }
}
