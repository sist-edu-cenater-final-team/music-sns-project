package com.github.musicsnsproject.config.client.oauth.dto.tokens;

import lombok.Getter;

@Getter
public class GithubTokens implements OAuthTokens{
    private String accessToken;
    private String tokenType;
    private String scope;
}
