package com.github.musicsnsproject.repository.jpa.account.socialid;

import com.github.musicsnsproject.common.converter.custom.OAuthProviderConverter;
import com.github.musicsnsproject.common.myenum.OAuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(of = { "socialId", "provider" })
@Embeddable
@Getter
public class SocialIdPk implements Serializable {

    private String socialId;

    @Convert(converter = OAuthProviderConverter.class)
    private OAuthProvider provider;

    public static SocialIdPk of(String socialId, OAuthProvider provider) {
        SocialIdPk socialIdPk = new SocialIdPk();
        socialIdPk.socialId = socialId;
        socialIdPk.provider = provider;
        return socialIdPk;
    }

}

