package com.github.musicsnsproject.web.controller.rest.account.auth;


import com.github.musicsnsproject.common.myenum.Gender;
import com.github.musicsnsproject.common.myenum.OAuthProvider;
import com.github.musicsnsproject.common.myenum.RoleEnum;
import com.github.musicsnsproject.service.account.auth.SignUpLoginService;
import com.github.musicsnsproject.web.dto.account.auth.request.LoginRequest;
import com.github.musicsnsproject.web.dto.account.auth.request.SignUpRequest;
import com.github.musicsnsproject.web.dto.account.auth.response.TokenResponse;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.github.musicsnsproject.common.MyUtils.createResponseEntity;
import static com.github.musicsnsproject.config.security.JwtProvider.*;

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
                .emptyData(HttpStatus.CREATED, "회원가입이 완료되었습니다!");
        return createResponseEntity(signUpResponse);
    }


    @Override
    @PostMapping("/login")
    public ResponseEntity<CustomSuccessResponse<TokenResponse>> login(@RequestBody @Valid LoginRequest loginRequest){
        TokenResponse tokenResponse = signUpLoginService.loginResponseToken(loginRequest);
        return createResponseEntity(
                CustomSuccessResponse.ofOk("로그인 성공", tokenResponse),
                tokenResponse.getRefreshTokenCookie()
        );
    }


    @Override
    @PostMapping("/refresh")
    public ResponseEntity<CustomSuccessResponse<TokenResponse>> regenerateToken(@RequestHeader(AUTH_HEADER_NAME) String authHeader,
                                                                                @CookieValue(value = REFRESH_COOKIE_NAME) String refreshToken
                                                                                ){
        String accessToken = authHeaderToToken(authHeader);
        TokenResponse tokenResponse = signUpLoginService.refreshTokenByTokens(accessToken, refreshToken);
        return createResponseEntity(
                CustomSuccessResponse.ofOk("토큰 재발급", tokenResponse),
                tokenResponse.getRefreshTokenCookie()
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<CustomSuccessResponse<Void>> logout(@RequestHeader(AUTH_HEADER_NAME) String authHeader
    ){
        String accessToken = authHeaderToToken(authHeader);
        ResponseCookie responseCookie = signUpLoginService.logoutInvalidationToken(accessToken);
        CustomSuccessResponse<Void> response = CustomSuccessResponse
                .emptyDataOk("로그아웃 되었습니다.");
        return createResponseEntity(response, responseCookie);
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
    @GetMapping("/pk")
    public CustomSuccessResponse<Long> pk(@AuthenticationPrincipal Long userId){
        return CustomSuccessResponse.ofOk("로그인 유저 PK 조회 성공", userId);
    }



}
