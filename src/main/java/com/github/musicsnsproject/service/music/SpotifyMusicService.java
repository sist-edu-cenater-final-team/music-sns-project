package com.github.musicsnsproject.service.music;

import com.github.musicsnsproject.common.exceptions.CustomBindException;
import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.common.exceptions.CustomServerException;
import com.github.musicsnsproject.common.myenum.MusicSearchType;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackArtist;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;
import com.github.musicsnsproject.web.dto.pageable.PaginationResponse;
import com.neovisionaries.i18n.CountryCode;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotifyMusicService {
    private final WebClient.Builder webClientBuilder;
    private final SpotifyApi spotifyApi;

    private List<TrackArtist> convertToTrackArtists(ArtistSimplified[] artists) {
        List<TrackArtist> trackArtists = new ArrayList<>();
        for (ArtistSimplified artist : artists) {
            trackArtists.add(TrackArtist.of(artist.getId(), artist.getName(), artist.getExternalUrls().get("spotify")));
        }
        return trackArtists;
    }
    private TrackResponse.TrackAlbum convertToTrackAlbum(AlbumSimplified album){
        LocalDate releaseDate = LocalDate.parse(album.getReleaseDate());
        return TrackResponse.TrackAlbum.builder()
                .albumId(album.getId())
                .albumName(album.getName())
                .albumSpotifyUrl(album.getExternalUrls().get("spotify"))
                .albumImageUrl(album.getImages() != null && album.getImages().length > 0 ? album.getImages()[0].getUrl() : null)
                .releaseDate(releaseDate)
                .albumType(album.getType().name())
                .build();
    }
    private String formatDuration(int durationMs) {
        int totalSeconds = durationMs / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    private TrackResponse convertToTrackResponse(Track track) {
        List<TrackArtist> artists = convertToTrackArtists(track.getArtists());
        TrackResponse.TrackAlbum album = convertToTrackAlbum(track.getAlbum());
        String duration = formatDuration(track.getDurationMs());

        return TrackResponse.builder()
                .trackId(track.getId())
                .trackName(track.getName())
                .duration(duration)
                .trackSpotifyUrl(track.getExternalUrls().get("spotify"))
                .trackPopularity(track.getPopularity())
                .trackNumber(track.getTrackNumber())
                .artist(artists)
                .album(album)
                .build();
    }

    private List<TrackResponse> convertToTrackResponse(Paging<Track> trackPaging){
        List<TrackResponse> trackResponses = new ArrayList<>();
        for (Track track : trackPaging.getItems()) {
            TrackResponse trackResponse = convertToTrackResponse(track);
            trackResponses.add(trackResponse);
        }
        return trackResponses;
    }


    public PaginationResponse<TrackResponse> searchTracksRequest(String keyword, MusicSearchType searchType, int page, int size) {
        try{
            SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(searchType.getPrefix() + keyword)
                    .market(CountryCode.KR)
                    .setHeader("Accept-Language", "ko-KR,ko;q=0.9")
                    .offset(page * size)
                    .limit(size)
                    .build();
            Paging<Track> trackPaging = searchTracksRequest.execute();

            List<TrackResponse> trackResponses = convertToTrackResponse(trackPaging);
            //size를 이용해 마지막 페이지 확인
            long totalItems = trackPaging.getTotal();
            long lastPage = ( totalItems + size - 1) / size;
            return PaginationResponse.of(page+1, size, lastPage, totalItems, trackResponses);
        }catch (IOException e){
            throw CustomBindException.of().systemMessage(e.getMessage()).customMessage("Spotify 통신 실패").build();
        } catch (ParseException e) {
            throw CustomNotAcceptException.of().customMessage("Spotify 변환 실패").systemMessage(e.getMessage()).build();
        } catch (SpotifyWebApiException e) {
            throw CustomServerException.of().customMessage("Spotify API 에러").systemMessage(e.getMessage()).build();
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
    //0nV8nwkfGIQyzvdfVVRaoW 띵곡 추천곡
    //6IZ7kJDFvRoM3EP0JTVEMi 유튜버 때껄룩
    //0uoSKc1UI9jVeKEN7b4SaW 꼬실때듣는 플리
    //45kbz93aYOGPoUwkRk7deL 느좋 플리
    //5ly5NEeyfsOYxfxPrIBEhr 1초 기억조작 플리
    //6wmGWoKWOjSBFjugPl6Fz8 들어본 J-Pop
    public Object getSpotifyChart(String playListId) {
        GetPlaylistRequest request = spotifyApi.getPlaylist(playListId)
                .build();
        try {
            Playlist playlist = request.execute();
            return playlist;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SpotifyWebApiException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
