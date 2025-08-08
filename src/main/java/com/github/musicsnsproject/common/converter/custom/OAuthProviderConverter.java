package com.github.musicsnsproject.common.converter.custom;

import com.github.musicsnsproject.common.myenum.OAuthProvider;
import org.springframework.stereotype.Component;

@Component
public class OAuthProviderConverter extends MyConverter<OAuthProvider> {
    public OAuthProviderConverter() {
        super(OAuthProvider.class);
    }
}
