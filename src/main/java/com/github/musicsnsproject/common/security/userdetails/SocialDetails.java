package com.github.musicsnsproject.common.security.userdetails;

import com.github.musicsnsproject.common.myenum.OAuthProvider;
import lombok.Getter;

@Getter
public class SocialDetails {
    private String socialId;
    private OAuthProvider provider;
}
