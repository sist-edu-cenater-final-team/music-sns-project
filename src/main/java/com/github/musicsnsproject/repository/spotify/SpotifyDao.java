package com.github.musicsnsproject.repository.spotify;

import com.github.musicsnsproject.common.exceptions.CustomBindException;
import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.common.exceptions.CustomServerException;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Repository;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.AbstractDataRequest;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.io.IOException;


@Repository
@RequiredArgsConstructor
public class SpotifyDao {
    private final SpotifyApi spotifyApi;

    public Paging<Track> findTracksByKeyword(String keyword, int page, int size){

        SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(keyword)
                .setHeader("Accept-Language", "ko-KR,ko;q=0.9")
                .offset(page * size)
                .limit(size+1)
                .build();
        return requestExecute(searchTracksRequest);
    }

    public Artist findArtistById(String artistId) {


        GetArtistRequest request = spotifyApi.getArtist(artistId)
                .setHeader("Accept-Language", "ko-KR,ko;q=0.9")
                .build();
        return requestExecute(request);
    }


    //execute 제네릭으로 추상화
    public <T> T requestExecute(AbstractDataRequest<T> request){
        try{
            return request.execute();
        }
        catch (IOException e){
            throw CustomBindException.of().systemMessage(e.getMessage()).customMessage("Spotify 통신 실패").build();
        } catch (ParseException e) {
            throw CustomNotAcceptException.of().customMessage("Spotify 변환 실패").systemMessage(e.getMessage()).build();
        } catch (SpotifyWebApiException e) {
            throw CustomServerException.of().customMessage("Spotify API 에러").systemMessage(e.getMessage()).build();
        }
    }


    public Paging<AlbumSimplified> findArtistAlbums(String artistId, int page, int size) {
        GetArtistsAlbumsRequest request = spotifyApi.getArtistsAlbums(artistId)
                .setHeader("Accept-Language", "ko-KR,ko;q=0.9")
                .offset(page*size)
                .limit(size+1) // Spotify API는 최대 50개까지 한 번에 요청 가능
                .build();
        return requestExecute(request);
    }

    public Album findAlbumById(String albumId) {
        GetAlbumRequest request = spotifyApi.getAlbum(albumId)
                .setHeader("Accept-Language", "ko-KR,ko;q=0.9")
                .build();
        return requestExecute(request);
    }
}
