package com.github.musicsnsproject.web.dto.account.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.musicsnsproject.web.dto.account.oauth.response.OAuthDtoInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseCookie;

@Getter
@Builder
public class TokenResponse implements OAuthDtoInterface {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjkwNjA0NjcsImV4cCI6MTcyOTA2NDA2Nywic3ViIjoiYWJjM0BhYmMuY29tIiwicm9sZXMiOiJST0xFX1VTRVIifQ.LeC81cXhFI1H_VlKcJlOzRmtR73ITIjqYdOsrPZqPZs")
    private String accessToken;
    @JsonIgnore
    private ResponseCookie refreshTokenCookie;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isConnection;

}
