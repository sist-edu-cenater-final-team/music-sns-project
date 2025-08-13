package com.github.musicsnsproject.service.account.auth;

import com.github.musicsnsproject.common.exceptions.CustomBadCredentialsException;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;
import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
@Primary
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MyUserRepository myUsersRepository;


    @Override
    public CustomUserDetails loadUserByUsername(String emailOrPhoneNumber) {
        return myUsersRepository.findByEmailOrPhoneNumberForAuth(emailOrPhoneNumber)
                .orElseThrow(() -> CustomBadCredentialsException.of()
                        .systemMessage("User Not Found")
                        .customMessage("존재 하지 않는 유저 입니다.")
                        .build());
    }
/*CustomAuthenticationProvider 사용으로 주석처리
    private void checkLockedOrDisable(MyUser myUser) {
        if (myUser.isLocked() && !myUser.isUnlockTime()) {
            throw AccountLockedException.of()
                    .customMessage("로그인 실패")
                    .request(new AuthFailureMessage(myUser))
                    .build();
        } else if (myUser.isDisabled()) {
            throw CustomAccessDenied.of()
                    .systemMessage("시스템")
                    .customMessage("로그인 실패")
                    .request(new AuthFailureMessage(myUser))
                    .build();
        }
    }*/
}
