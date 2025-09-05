//package com.github.musicsnsproject.web.interceptors;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.github.musicsnsproject.common.event.CustomAuthenticationEntryPoint;
//import com.github.musicsnsproject.common.exceptions.CustomBadCredentialsException;
//import com.github.musicsnsproject.config.security.JwtProvider;
//import com.github.musicsnsproject.web.dto.response.CustomErrorResponse;
//import com.nimbusds.jose.util.StandardCharset;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import java.io.IOException;
//import java.util.Map;
//
//import static com.github.musicsnsproject.config.security.JwtProvider.*;
//
//@RequiredArgsConstructor
//@Log4j2
//public class JwtHandshakeInterceptor implements HandshakeInterceptor {
//    private final JwtProvider jwtProvider;
//
//    @Override
//    public boolean beforeHandshake(@NotNull ServerHttpRequest request,
//                                   @NotNull ServerHttpResponse response,
//                                   @NotNull WebSocketHandler wsHandler,
//                                   @NotNull Map<String, Object> attributes) throws Exception {
//        String authHeader = request.getHeaders().getFirst(AUTH_HEADER_NAME);
//        String token = authHeaderToToken(authHeader);
//        if(token!=null){
//            try {
//                Object userId = jwtProvider.getUserIdFromToken(token);
//                attributes.put("userId", userId);
//                return true;
//            }
//            catch (Exception e){
//                log.error("소켓연결중 토큰 파싱 에러", e);
//                return false;
//            }
//        }
//        return false;
//    }
//
//
//    @Override
//    public void afterHandshake(@NotNull ServerHttpRequest request,
//                               @NotNull ServerHttpResponse response,
//                               @NotNull WebSocketHandler wsHandler,
//                               Exception exception) {
//
//    }
//}
