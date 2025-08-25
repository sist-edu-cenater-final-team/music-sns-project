package com.github.musicsnsproject.web.controller.rest.account.auth;

import com.github.musicsnsproject.service.account.PhoneVerifyService;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phone")
@RequiredArgsConstructor
public class PhoneVerifyController {
    private final PhoneVerifyService phoneVerifyService;
    @PostMapping("/send")
    public CustomSuccessResponse<Void> sendMessage(@RequestParam String phoneNumber) {
        phoneVerifyService.sendMessage(phoneNumber);
        return CustomSuccessResponse.emptyDataOk("인증 문자 전송 완료");
    }
    @GetMapping("/verify")
    public CustomSuccessResponse<Boolean> verifyMessage(@RequestParam String phoneNumber, @RequestParam String code) {
        boolean isVerified = phoneVerifyService.verifyCode(phoneNumber, code);
        String message = isVerified ? "인증번호가 일치합니다." : "인증번호가 일치하지 않습니다.";
        return CustomSuccessResponse.ofOk(message, isVerified);
    }
    @GetMapping("/duplicate")
    public CustomSuccessResponse<Boolean> duplicateCheckPhoneNumber(@RequestParam String phoneNumber) {
        boolean isPhoneNumberAvailable = phoneVerifyService.duplicateCheckPhoneNumber(phoneNumber);
        return CustomSuccessResponse
                .ofOk(isPhoneNumberAvailable ? "핸드폰 번호 사용 가능" : "핸드폰 번호 중복",
                        isPhoneNumberAvailable);
    }
}
