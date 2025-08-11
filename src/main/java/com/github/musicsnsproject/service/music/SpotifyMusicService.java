package com.github.musicsnsproject.service.music;

import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SpotifyMusicService {
    private final WebClient.Builder webClientBuilder;
    private final SpotifyApi spotifyApi;


    public Paging<Track> searchTracksRequest(String keyword) {
        SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(keyword)
                .limit(10)
                .build();
        try {
            Paging<Track> paging = searchTracksRequest.execute();
            return paging;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SpotifyWebApiException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public Object getMusicById(String musicId){
        try {
            Track a = spotifyApi.getTrack(musicId).build().execute();
            return a;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SpotifyWebApiException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
