package com.github.musicsnsproject.config.client.oauth.code;

import com.github.musicsnsproject.common.myenum.OAuthProvider;
import org.springframework.web.util.UriComponents;

public abstract class OAuthCodeUrlGenerator {

    public String getAuthorizationUrl(String redirectUrl) {
        return baseAuthCodeUrl() + getParam(redirectUrl).toUriString();
    }
    public abstract OAuthProvider oAuthProvider();

    protected abstract UriComponents getParam(String redirectUrl);
    protected abstract String baseAuthCodeUrl();
}
