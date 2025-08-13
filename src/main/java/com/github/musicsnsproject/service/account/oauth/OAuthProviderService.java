package com.github.musicsnsproject.service.account.oauth;

import com.github.musicsnsproject.common.myenum.OAuthProvider;
import com.github.musicsnsproject.config.properties.server.ServerUrlFields;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OAuthProviderService {
    private final OAuthCodeManager oAuthCodeManager;
    private final Function<ServerUrlFields, String> requestBaseUrlProvider;


    public String getOAuthLoginPageUrl(OAuthProvider oAuthProvider, HttpServletRequest httpServletRequest) {
        String baseUrl = getRequestBaseUrl(httpServletRequest);
        String redirectUrl = baseUrl + "/api/oauth/" +oAuthProvider.name().toLowerCase()+"/callback";
        return oAuthCodeManager.getAuthorizationUrl(oAuthProvider, redirectUrl);
    }

    private String getRequestBaseUrl(HttpServletRequest request) {
        ServerUrlFields fields = ServerUrlFields.fromRequest(request);
        return requestBaseUrlProvider.apply(fields);
    }
}
