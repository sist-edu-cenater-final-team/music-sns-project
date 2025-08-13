package com.github.musicsnsproject.service.account;

import com.github.musicsnsproject.common.AccountServiceModule;
import com.github.musicsnsproject.common.converter.mapper.UserMapper;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.web.dto.account.auth.response.MyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountServiceModule accountServiceModule;

    public MyInfoResponse myInfoByEmail(String principal) {
        MyUser myUser = accountServiceModule.findMyUserFetchJoin(principal);
        return UserMapper.INSTANCE.myUserToAccountDto(myUser);
    }


}
