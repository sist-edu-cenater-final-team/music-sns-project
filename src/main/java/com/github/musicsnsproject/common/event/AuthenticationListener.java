package com.github.musicsnsproject.common.event;

import com.github.musicsnsproject.common.AccountServiceModule;
import com.github.musicsnsproject.common.exceptions.CustomBadCredentialsException;
import com.github.musicsnsproject.common.myenum.UserStatus;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.web.dto.account.auth.response.AuthFailureMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

@Component
@RequiredArgsConstructor
public class AuthenticationListener {

    private final AccountServiceModule accountServiceModule;

    @EventListener
    public void handleAuthFailEvent(AuthenticationFailureBadCredentialsEvent event) {
        CustomUserDetails userDetails = (CustomUserDetails) event.getException().getAuthenticationRequest().getPrincipal();
        accountServiceModule.failureCounting(userDetails);
        throw CustomBadCredentialsException.of()
                .systemMessage(userDetails.getStatus() == UserStatus.LOCK ? event.getException().getMessage() + " 계정이 잠깁니다."
                        : event.getException().getMessage())
                .customMessage("비밀번호 오류")
                .request(new AuthFailureMessage(userDetails))
                .build();
    }

//
//    @EventListener
//    public void handleAuthFailEvent(CustomAuthFailEvent event){
//        CustomUserDetails userDetails = event.getException().getUser();
//        accountServiceModule.failureCounting(userDetails);
//
//        throw CustomBadCredentialsException.of()
//                .systemMessage(userDetails.getStatus() == UserStatus.LOCK?event.getException().getMessage()+" 계정이 잠깁니다."
//                        :event.getException().getMessage())
//                .customMessage("비밀번호 오류")
//                .request(new AuthFailureMessage(userDetails))
//                .build();
//    }

    @EventListener
    public void handleAuthSuccessEvent(AuthenticationSuccessEvent event) {
        CustomUserDetails customUserDetails = (CustomUserDetails) event.getAuthentication().getPrincipal();
        accountServiceModule.loginSuccessEvent(customUserDetails);

    }
}


