package com.github.musicsnsproject.service.music;

import com.github.musicsnsproject.common.myenum.MusicSearchType;
import com.github.musicsnsproject.repository.spotify.SpotifyDao;
import com.github.musicsnsproject.web.dto.music.spotify.SimplifiedAlbum;
import com.github.musicsnsproject.web.dto.music.spotify.album.AlbumResponse;
import com.github.musicsnsproject.web.dto.music.spotify.artist.ArtistResponse;
import com.github.musicsnsproject.web.dto.music.spotify.SimplifiedArtist;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;
import com.github.musicsnsproject.web.dto.pageable.ScrollResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotifyMusicService {
    private final WebClient.Builder webClientBuilder;
    private final SpotifyDao spotifyDao;


    private List<SimplifiedArtist> convertToSimplifiedArtist(ArtistSimplified[] artists) {
        List<SimplifiedArtist> simplifiedArtists = new ArrayList<>();
        for (ArtistSimplified artist : artists) {
            simplifiedArtists.add(SimplifiedArtist.of(artist.getId(), artist.getName(), artist.getExternalUrls().get("spotify")));
        }
        return simplifiedArtists;
    }

    private LocalDate convertToLocalDate(String releaseDate) {
        if (releaseDate == null || releaseDate.isEmpty()) {
            return null; // or handle as needed
        }
        if (releaseDate.length() == 4) {
            // If the release date is just a year, return January 1st of that year
            return LocalDate.of(Integer.parseInt(releaseDate), 1, 1);
        }
        if (releaseDate.length() == 7) {
            // If the release date is in the format "YYYY-MM", return the first day of that month
            String[] parts = releaseDate.split("-");
            return LocalDate.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 1);
        }
        return LocalDate.parse(releaseDate);
    }

    private SimplifiedAlbum convertToSimplifiedAlbum(AlbumSimplified album) {
        LocalDate releaseDate = convertToLocalDate(album.getReleaseDate());
        return SimplifiedAlbum.builder()
                .albumId(album.getId())
                .albumName(album.getName())
                .albumSpotifyUrl(album.getExternalUrls().get("spotify"))
                .albumImageUrl(album.getImages() != null && album.getImages().length > 0 ? album.getImages()[0].getUrl() : null)
                .releaseDate(releaseDate)
                .albumType(album.getType().name())
                .build();
    }
    private SimplifiedAlbum convertToSimplifiedAlbum(AlbumSimplified album, List<SimplifiedArtist> artists) {
        LocalDate releaseDate = convertToLocalDate(album.getReleaseDate());
        return SimplifiedAlbum.builder()
                .albumId(album.getId())
                .albumName(album.getName())
                .albumSpotifyUrl(album.getExternalUrls().get("spotify"))
                .albumImageUrl(album.getImages() != null && album.getImages().length > 0 ? album.getImages()[0].getUrl() : null)
                .releaseDate(releaseDate)
                .albumType(album.getType().name())
                .artists(artists)
                .build();
    }

    private String formatDuration(int durationMs) {
        int totalSeconds = durationMs / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private TrackResponse convertToTrackResponse(Track track) {
        List<SimplifiedArtist> artists = convertToSimplifiedArtist(track.getArtists());
        SimplifiedAlbum album = convertToSimplifiedAlbum(track.getAlbum());
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

    private SimplifiedAlbum convertToSimplified(AlbumSimplified element){
        List<SimplifiedArtist> simplifiedArtists = convertToSimplifiedArtist(element.getArtists());
        return convertToSimplifiedAlbum(element, simplifiedArtists);
    }

    private List<TrackResponse> convertToTrackResponse(Paging<Track> trackPaging) {
        List<TrackResponse> trackResponses = new ArrayList<>();
        for (Track track : trackPaging.getItems()) {
            TrackResponse trackResponse = convertToTrackResponse(track);
            trackResponses.add(trackResponse);
        }
        return trackResponses;
    }

    private List<SimplifiedAlbum> convertToSimplified(Paging<AlbumSimplified> artistAlbums) {
        List<SimplifiedAlbum> responseList = new ArrayList<>();
        for (AlbumSimplified element : artistAlbums.getItems()) {
            SimplifiedAlbum response = convertToSimplified(element);
            responseList.add(response);
        }
        return responseList;
    }


    public ScrollResponse<TrackResponse> searchTracksRequest(String keyword, MusicSearchType searchType, int page, int size) {
        String searchKeyword = searchType.getPrefix() + keyword.trim();
        Paging<Track> trackPaging = spotifyDao.findTracksByKeyword(searchKeyword, page, size);

        List<TrackResponse> trackResponses = convertToTrackResponse(trackPaging);
        //size를 이용해 마지막 페이지 확인  하지만 spotify는 마지막 페이지가 없어서 스크롤 방식으로 수정
//            long totalItems = trackPaging.getTotal();
//            long lastPage = ( totalItems + size - 1) / size;
        return ScrollResponse.of(page + 1, size, trackResponses);
    }
    private ArtistResponse convertToArtistResponse(Artist artist){
        return ArtistResponse.builder()
                .artistId(artist.getId())
                .artistName(artist.getName())
                .artistSpotifyUrl(artist.getExternalUrls().get("spotify"))
                .artistPopularity(artist.getPopularity())
                .totalFollowers(artist.getFollowers()!=null?artist.getFollowers().getTotal() : 0)
                .artistGenres(artist.getGenres() != null ? List.of(artist.getGenres()) : List.of())
                .artistImageUrl(artist.getImages() != null && artist.getImages().length > 0 ? artist.getImages()[0].getUrl() : null)
                .build();
    }

    public ArtistResponse searchArtistById(String artistId) {
        Artist artist = spotifyDao.findArtistById(artistId);
        return convertToArtistResponse(artist);
    }
    public ScrollResponse<SimplifiedAlbum> searchArtistAlbums(String artistId, int page, int size) {
        Paging<AlbumSimplified> artistAlbums = spotifyDao.findArtistAlbums(artistId, page, size);
        List<SimplifiedAlbum> simplifiedAlbums = convertToSimplified(artistAlbums);
        return ScrollResponse.of(page + 1, size, simplifiedAlbums);

    }


    public AlbumResponse searchAlbumById(String albumId) {
        Album album = spotifyDao.findAlbumById(albumId);

        return null;
    }

    public TrackResponse getTrackResponseById(String trackId) {
        Track track = spotifyDao.findTrackById(trackId);
        return convertToTrackResponse(track);
    }
}


//0nV8nwkfGIQyzvdfVVRaoW 띵곡 추천곡
//6IZ7kJDFvRoM3EP0JTVEMi 유튜버 때껄룩
//0uoSKc1UI9jVeKEN7b4SaW 꼬실때듣는 플리
//45kbz93aYOGPoUwkRk7deL 느좋 플리
//5ly5NEeyfsOYxfxPrIBEhr 1초 기억조작 플리
//6wmGWoKWOjSBFjugPl6Fz8 들어본 J-Pop
//public Object getSpotifyChart(String playListId) {
//    GetPlaylistRequest request = spotifyApi.getPlaylist(playListId)
//            .build();
//    try {
//        Playlist playlist = request.execute();
//        return playlist;
//    } catch (IOException e) {
//        throw new RuntimeException(e);
//    } catch (SpotifyWebApiException e) {
//        throw new RuntimeException(e);
//    } catch (ParseException e) {
//        throw new RuntimeException(e);
//    }
//}


