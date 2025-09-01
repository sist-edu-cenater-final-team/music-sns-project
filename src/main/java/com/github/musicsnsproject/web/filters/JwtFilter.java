package com.github.musicsnsproject.web.filters;

import com.github.musicsnsproject.config.security.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.github.musicsnsproject.config.security.JwtProvider.*;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTH_HEADER_NAME);
        String token = authHeaderToToken(authHeader);

        if(token!=null){
            try {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (Exception e){
                request.setAttribute(AUTH_EXCEPTION_NAME, e);
            }
        }
        filterChain.doFilter(request, response);
    }


    @Override//필터를 적용시키지 않을 url true 값이 배출되면 필터는 실행되지 않는다.
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().contains("/swagger")
                ||request.getRequestURI().contains("/api-docs")
                ||(request.getRequestURI().startsWith("/api/auth")&&!request.getRequestURI().startsWith("/api/auth/pk"))
                ||
                (!request.getRequestURI().startsWith("/api") && // /api가 아니고 chat, rooms 도 아니면 필터 적용 안함
                        !request.getRequestURI().startsWith("/chat") &&
                        !request.getRequestURI().startsWith("/rooms"));
    }


}