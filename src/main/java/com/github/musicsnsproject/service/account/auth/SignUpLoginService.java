package com.github.musicsnsproject.service.account.auth;

import com.github.musicsnsproject.common.AccountServiceModule;
import com.github.musicsnsproject.common.converter.mapper.UserMapper;
import com.github.musicsnsproject.common.exceptions.CustomBadCredentialsException;
import com.github.musicsnsproject.common.exceptions.CustomBadRequestException;
import com.github.musicsnsproject.common.exceptions.CustomServerException;
import com.github.musicsnsproject.common.myenum.RoleEnum;
import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.config.security.JwtProvider;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;
import com.github.musicsnsproject.web.dto.account.auth.request.LoginRequest;
import com.github.musicsnsproject.web.dto.account.auth.request.SignUpRequest;
import com.github.musicsnsproject.web.dto.account.auth.response.TokenResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SignUpLoginService {

    private final MyUserRepository myUsersRepository;
    private final AccountServiceModule accountServiceModule;
    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;


    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        //비번 암호화
        signUpRequest.passwordReplace(passwordEncoder.encode(signUpRequest.getPassword()));

        MyUser signUpMyUser = UserMapper.INSTANCE.accountDtoToMyUser(signUpRequest);
        signUpMyUser.setBeginRole(RoleEnum.ROLE_USER);
        //세이브 실행하면서 중복값 발생시 발생되는 익셉션 예외처리
        try {
            myUsersRepository.save(signUpMyUser);
        } catch (DateTimeException e) {
            throw CustomBadRequestException.of()
                    .systemMessage(e.getMessage())
                    .customMessage("호환되지 않는 날짜 형식 (ex. yyyy-M-d)")
                    .request(signUpRequest.getDateOfBirth())
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public TokenResponse loginResponseToken(LoginRequest loginRequest) {
        CustomUserDetails details = customUserDetailsService.loadUserByUsername(loginRequest.getEmailOrPhoneNumber());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(details, loginRequest.getPassword(), details.getAuthorities())
        );
        String roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(","));

        String accessToken = jwtProvider.createNewAccessToken(authentication.getName(), roles);
        String refreshToken = jwtProvider.createNewRefreshToken();
        try {
            return jwtProvider.saveRefreshTokenAndCreateTokenDto(accessToken, refreshToken);
        } catch (RedisConnectionFailureException e) {
            throw CustomServerException.of()
                    .systemMessage(e.getMessage()+"   "+e.getCause().getMessage())
                    .customMessage("Redis 서버 연결 실패")
                    .build();
        }
    }

    public TokenResponse refreshTokenByTokens(String accessToken, String refreshToken) {
        try {
            return jwtProvider.tokenRefresh(accessToken, refreshToken);
        } catch (RedisConnectionFailureException e) {
            throw CustomServerException.of()
                    .systemMessage(e.getMessage()+"   "+e.getCause().getMessage())
                    .customMessage("Redis 서버 연결 실패")
                    .build();
        } catch (ExpiredJwtException | NoSuchElementException e) {
            throw CustomBadCredentialsException.of()
                    .systemMessage(e.getMessage())
                    .customMessage(e instanceof ExpiredJwtException ? "리프레시 토큰 만료" : "재발급 받을 수 없는 액세스 토큰")
                    .build();
        }

    }


    public ResponseCookie logoutInvalidationToken(String accessToken) {
        try{
            ResponseCookie cookie = jwtProvider.deleteRefreshToken(accessToken);
            jwtProvider.blackListAccessToken(accessToken);
            return cookie;
        }catch (RedisConnectionFailureException e){
            throw CustomServerException.of()
                    .systemMessage(e.getMessage()+"   "+e.getCause().getMessage())
                    .customMessage("Redis 서버 연결 실패")
                    .build();
        }
    }
}
