package com.github.musicsnsproject.web.controller.rest.account;
import com.github.musicsnsproject.service.account.AccountService;
import com.github.musicsnsproject.web.dto.account.auth.response.MyInfoResponse;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController implements AccountControllerDocs {
    private final AccountService accountService;
    @GetMapping("/my-info")
    public CustomSuccessResponse<MyInfoResponse> getMyInfo(@AuthenticationPrincipal String principal){
        return CustomSuccessResponse
                .ofOk("유저 정보 조회 성공", accountService.myInfoByEmail(principal));
    }
    @GetMapping("/nickname/duplicate")
    public CustomSuccessResponse<Boolean> duplicateCheckNickname(String nickname){
        boolean isNicknameAvailable = accountService.duplicateCheckNickname(nickname);
        return CustomSuccessResponse
                .ofOk(isNicknameAvailable ? "닉네임 사용 가능":"닉네임 중복",
                        isNicknameAvailable);
    }

}
