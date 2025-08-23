package com.github.musicsnsproject.web.controller.rest.account.auth;


import com.github.musicsnsproject.common.myenum.Gender;
import com.github.musicsnsproject.common.myenum.OAuthProvider;
import com.github.musicsnsproject.common.myenum.RoleEnum;
import com.github.musicsnsproject.service.account.auth.SignUpLoginService;
import com.github.musicsnsproject.web.dto.account.auth.request.LoginRequest;
import com.github.musicsnsproject.web.dto.account.auth.request.SignUpRequest;
import com.github.musicsnsproject.web.dto.account.auth.response.TokenDto;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
    private final SignUpLoginService signUpLoginService;



    @Override
    @PostMapping("/sign-up")
    public ResponseEntity<CustomSuccessResponse<Void>> signUp(@RequestBody @Valid SignUpRequest signUpRequest){
        signUpLoginService.signUp(signUpRequest);
        CustomSuccessResponse<Void> signUpResponse = CustomSuccessResponse
                .emptyData(HttpStatus.CREATED, "회원가입 완료");
        return ResponseEntity
                .status(signUpResponse.getHttpStatus())
                .body(signUpResponse);
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        // RefreshToken을 HttpOnly 쿠키로 설정
        return ResponseCookie.from("RefreshToken", refreshToken)
                .httpOnly(true) // JavaScript로 접근 불가
                .path("/") // 전체 경로에서 사용
                .maxAge(Duration.ofDays(7)) // 7일
                .sameSite("Strict") // SameSite 설정 CSRF 공격 방지
//                .secure(true) // HTTPS에서만 전송 (개발 환경에서는 주석 처리)
                .build();
    }
    private ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("RefreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) // 즉시 만료
                .sameSite("Strict")
                .build();
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<CustomSuccessResponse<TokenDto>> login(@RequestBody @Valid LoginRequest loginRequest){
        TokenDto tokenDto = signUpLoginService.loginResponseToken(loginRequest);
        ResponseCookie cookie = createRefreshTokenCookie(tokenDto.getRefreshToken());
        CustomSuccessResponse<TokenDto> response = CustomSuccessResponse
                .ofOk("로그인 성공", tokenDto);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
    @Override
    @PostMapping("/refresh")
    public ResponseEntity<CustomSuccessResponse<TokenDto>> regenerateToken(@RequestHeader("Authorization") String authHeader,
                                                           @CookieValue(value = "RefreshToken") String refreshToken){
        String accessToken = authHeader.replace("Bearer ", "");
        TokenDto tokenDto = TokenDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        TokenDto responseToken = signUpLoginService.refreshTokenByTokenDto(tokenDto);
        CustomSuccessResponse<TokenDto> response = CustomSuccessResponse
                .ofOk("토큰 재발급", responseToken);
        ResponseCookie cookie = createRefreshTokenCookie(responseToken.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<CustomSuccessResponse<Void>> logout(@RequestHeader("Authorization") String authHeader
                                                              ){
        String accessToken = authHeader.replace("Bearer ", "");
        signUpLoginService.logoutInvalidationToken(accessToken);
        ResponseCookie cookie = deleteRefreshTokenCookie();
        CustomSuccessResponse<Void> response = CustomSuccessResponse
                .emptyDataOk("로그아웃 성공");
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }



    @GetMapping("/tt")
    public CustomSuccessResponse<OAuthProvider> tt(@Parameter(schema = @Schema(type = "string", example = "카카오")) @RequestParam OAuthProvider provider){
        return CustomSuccessResponse.ofOk(provider.name(), provider);
    }
    @GetMapping("/ttt")
    public CustomSuccessResponse<Gender> ttt(@Parameter(schema = @Schema(type = "string", example = "카카오")) @RequestParam Gender gender){
        return CustomSuccessResponse.ofOk(gender.name(), gender);
    }
    @GetMapping("/tttt")
    public CustomSuccessResponse<RoleEnum> tttt(@Parameter(schema = @Schema(type = "string", example = "카카오")) @RequestParam RoleEnum role){
        return CustomSuccessResponse.ofOk(role.name(), role);
    }



}
