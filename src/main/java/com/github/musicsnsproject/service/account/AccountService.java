package com.github.musicsnsproject.service.account;

import com.github.musicsnsproject.common.AccountServiceModule;
import com.github.musicsnsproject.common.converter.mapper.UserMapper;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;
import com.github.musicsnsproject.web.dto.account.auth.response.MyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountServiceModule accountServiceModule;
    private final MyUserRepository myUserRepository;

    public MyInfoResponse myInfoByEmail(String principal) {
        MyUser myUser = accountServiceModule.findMyUserFetchJoin(principal);
        return UserMapper.INSTANCE.myUserToAccountDto(myUser);
    }

    @Transactional(readOnly = true)
    public boolean duplicateCheckNickname(String nickname) {
        return !myUserRepository.existsByNickname(nickname);
    }
}
