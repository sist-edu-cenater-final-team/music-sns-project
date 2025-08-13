package com.github.musicsnsproject.repository.jpa.account.user;


import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.repository.jpa.account.socialid.SocialIdPk;

import java.util.Optional;

public interface MyUserQueryRepository {

    Optional<MyUser> findBySocialIdPkOrUserEmail(SocialIdPk socialIdPk, String email);

    Optional<CustomUserDetails> findByEmailOrPhoneNumberForAuth(String emailOrPhoneNumber);

    void updateFailureCountByEmail(CustomUserDetails failUser);

//    Optional<CustomUserDetails> findBySocialIdPkOrUserEmailForAuth(SocialIdPk socialIdPk, String email);
}
