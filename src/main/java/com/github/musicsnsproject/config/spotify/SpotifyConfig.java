package com.github.musicsnsproject.config.spotify;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.util.Date;
import java.util.function.Function;

@Configuration
public class SpotifyConfig {
    @Value("${spotify.client-id}")
    private String clientId;
    @Value("${spotify.client-secret}")
    private String clientSecret;

    private SpotifyApi spotifyApi;
    private volatile String currentAccessToken;
    private volatile long tokenExpiresAt;


    @Bean
    public SpotifyApi spotifyApi() {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        refreshAccessToken(); // 초기 토큰 발급
//        scheduleTokenRefresh(); // 자동 갱신 스케줄링

        return this.spotifyApi;
    }
    private void refreshAccessToken() {
        try {
            ClientCredentialsRequest request = spotifyApi.clientCredentials().build();
            ClientCredentials credentials = request.execute();

            this.currentAccessToken = credentials.getAccessToken();
            this.tokenExpiresAt = System.currentTimeMillis() + (credentials.getExpiresIn() * 1000L);
            this.spotifyApi.setAccessToken(currentAccessToken);

            System.out.println("Spotify 토큰 갱신 완료. 만료시간: " + new Date(tokenExpiresAt));
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException("Spotify API 토큰 발급 실패", e);
        }
    }
    @Bean
    public Function<Void, Void> refreshSpotifyToken() {
        return (Void) -> {
            refreshAccessToken();
            return null;
        };
    }
//
//    @Scheduled(fixedRate = 3300000) // 55분마다 실행 (3300초)
//    public void scheduleTokenRefresh() {
//        System.out.println("토큰 갱신 스케줄 실행 중...");
//        refreshAccessToken();
//    }


}
