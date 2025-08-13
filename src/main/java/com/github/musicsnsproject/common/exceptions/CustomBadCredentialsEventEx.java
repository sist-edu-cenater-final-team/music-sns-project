package com.github.musicsnsproject.common.exceptions;

import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import lombok.Getter;
import org.springframework.security.authentication.BadCredentialsException;

@Getter
public class CustomBadCredentialsEventEx extends BadCredentialsException {
    private final CustomUserDetails user;
    private CustomBadCredentialsEventEx (String message, CustomUserDetails user){
        super(message);
        this.user = user;
    }

    public static CustomBadCredentialsEventEx of(String message, CustomUserDetails user) {
        return new CustomBadCredentialsEventEx(message, user);
    }
}
