package com.github.musicsnsproject.service.music;

import com.github.musicsnsproject.common.myenum.MusicSearchType;
import com.github.musicsnsproject.repository.spotify.SpotifyDao;
import com.github.musicsnsproject.repository.spotify.wrapper.album.AlbumBase;
import com.github.musicsnsproject.repository.spotify.wrapper.album.AlbumSimplifiedWrapper;
import com.github.musicsnsproject.repository.spotify.wrapper.album.AlbumWrapper;
import com.github.musicsnsproject.repository.spotify.wrapper.artist.ArtistBase;
import com.github.musicsnsproject.repository.spotify.wrapper.artist.ArtistSimplifiedWrapper;
import com.github.musicsnsproject.repository.spotify.wrapper.artist.ArtistWrapper;
import com.github.musicsnsproject.repository.spotify.wrapper.track.TrackBase;
import com.github.musicsnsproject.repository.spotify.wrapper.track.TrackSimplifiedWrapper;
import com.github.musicsnsproject.repository.spotify.wrapper.track.TrackWrapper;
import com.github.musicsnsproject.web.dto.music.spotify.album.SimplifiedAlbum;
import com.github.musicsnsproject.web.dto.music.spotify.search.RecommendSearch;
import com.github.musicsnsproject.web.dto.music.spotify.track.SimplifiedTrack;
import com.github.musicsnsproject.web.dto.music.spotify.album.AlbumResponse;
import com.github.musicsnsproject.web.dto.music.spotify.artist.ArtistResponse;
import com.github.musicsnsproject.web.dto.music.spotify.artist.SimplifiedArtist;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponseV1;
import com.github.musicsnsproject.web.dto.pageable.ScrollResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SpotifyMusicService {
    private final SpotifyDao spotifyDao;




    private SimplifiedArtist convertToSimplifiedArtist(ArtistBase artist) {
        return SimplifiedArtist.of(
                artist.getId(),
                artist.getName(),
                artist.getExternalUrls().get("spotify"),
                artist.getType().name()
        );
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

    private String formatDuration(int durationMs) {
        int totalSeconds = durationMs / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    //TODO: 리팩터링필요 (제거예정)
    private TrackResponseV1 convertToTrackResponseV1(Track track) {
        List<SimplifiedArtist> artists = convertSpotifyListToResponseList(track.getArtists(),
                element -> convertToSimplifiedArtist(ArtistSimplifiedWrapper.of(element)));
        AlbumBase albumBase = AlbumSimplifiedWrapper.of(track.getAlbum());
        SimplifiedAlbum album = convertToSimplifiedAlbum(albumBase);
        String duration = formatDuration(track.getDurationMs());

        return TrackResponseV1.builder()
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

    private TrackResponse convertToTrackResponse(Track track) {
        TrackBase trackBase = TrackWrapper.of(track);
        SimplifiedTrack simplifiedTrack = convertToSimplifiedTrack(trackBase);
        AlbumBase albumBase = AlbumSimplifiedWrapper.of(track.getAlbum());
        SimplifiedAlbum simplifiedAlbum = convertToSimplifiedAlbum(albumBase);
        return TrackResponse.of(simplifiedTrack, simplifiedAlbum, track.getPopularity());
    }


    private <T, R> List<R> convertSpotifyListToResponseList(T[] arr, Function<T, R> converterFunction) {
        List<R> responseList = new ArrayList<>();
        for (T element : arr) {
            R response = converterFunction.apply(element);
            responseList.add(response);
        }
        return responseList;
    }


    public ScrollResponse<TrackResponse> searchTracksRequest(String keyword, MusicSearchType searchType, int page, int size) {
        String searchKeyword = searchType.getPrefix() + keyword.trim();
        Paging<Track> trackPaging = spotifyDao.findTracksByKeyword(searchKeyword, page, size);
        List<TrackResponse> trackResponses = convertSpotifyListToResponseList(trackPaging.getItems(),
                this::convertToTrackResponse);
        //size를 이용해 마지막 페이지 확인  하지만 spotify는 마지막 페이지가 없어서 스크롤 방식으로 수정
//            long totalItems = trackPaging.getTotal();
//            long lastPage = ( totalItems + size - 1) / size;
        return ScrollResponse.of(page + 1, size, trackResponses);
    }


    private ArtistResponse convertToArtistResponse(Artist artist) {
        ArtistBase artistBase = ArtistWrapper.of(artist);
        SimplifiedArtist simplifiedArtist = convertToSimplifiedArtist(artistBase);
        return ArtistResponse.builder()
                .artist(simplifiedArtist)
                .artistPopularity(artist.getPopularity())
                .totalFollowers(artist.getFollowers() != null ? artist.getFollowers().getTotal() : 0)
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
        List<SimplifiedAlbum> simplifiedAlbums = convertSpotifyListToResponseList(artistAlbums.getItems(),
                element -> convertToSimplifiedAlbum(AlbumSimplifiedWrapper.of(element)));
        return ScrollResponse.of(page + 1, size, simplifiedAlbums);

    }


    public AlbumResponse searchAlbumById(String albumId) {
        Album album = spotifyDao.findAlbumById(albumId);
        return convertToAlbumResponse(album);
    }

    private AlbumResponse convertToAlbumResponse(Album album) {
        List<SimplifiedTrack> tracks = convertSpotifyListToResponseList(album.getTracks().getItems(),
                element -> convertToSimplifiedTrack(TrackSimplifiedWrapper.of(element)));
        AlbumBase albumBase = AlbumWrapper.of(album);
        SimplifiedAlbum simplifiedAlbum = convertToSimplifiedAlbum(albumBase);

        List<String> genres = album.getGenres() != null ? List.of(album.getGenres()) : List.of();
        List<String> copyrights = album.getCopyrights() != null ?
                Arrays.stream(album.getCopyrights()).map(Copyright::getText).toList()
                : List.of();

        return AlbumResponse.builder()
                .album(simplifiedAlbum)
                .genres(genres)
                .copyrights(copyrights)
                .label(album.getLabel())
                .tracks(tracks)
                .popularity(album.getPopularity())
                .build();
    }


    private SimplifiedTrack convertToSimplifiedTrack(TrackBase track) {
        List<SimplifiedArtist> artists = convertSpotifyListToResponseList(track.getArtists(),
                element -> convertToSimplifiedArtist(ArtistSimplifiedWrapper.of(element)));
        String duration = formatDuration(track.getDurationMs());
        return SimplifiedTrack.builder()
                .trackId(track.getId())
                .trackName(track.getName())
                .duration(duration)
                .durationMs(track.getDurationMs())
                .trackSpotifyUrl(track.getExternalUrls().get("spotify"))
                .trackNumber(track.getTrackNumber())
                .trackType(track.getType().name())
                .artists(artists)
                .build();

    }


    private SimplifiedAlbum convertToSimplifiedAlbum(AlbumBase album) {
        List<SimplifiedArtist> artists = convertSpotifyListToResponseList(album.getArtists(),
                element -> convertToSimplifiedArtist(ArtistSimplifiedWrapper.of(element)));
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

    //TODO: 리팩터링 필요 (제거예정)
    public TrackResponseV1 getTrackResponseById(String trackId) {
        Track track = spotifyDao.findTrackById(trackId);
        return convertToTrackResponseV1(track);
    }
    public TrackResponse getTrackResponseV2ById(String trackId) {
        Track track = spotifyDao.findTrackById(trackId);
        return convertToTrackResponse(track);
    }


    private void artistArrToRecommendArtistList(ArtistSimplified[] artistArr, List<RecommendSearch.RecommendArtist> response){
        for (ArtistSimplified artist : artistArr) {
            RecommendSearch.RecommendArtist recommendArtist = RecommendSearch.RecommendArtist.of(
                    artist.getId(),
                    artist.getName()
            );
            response.add(recommendArtist);
        }
    }
    private <T> List<T> getRandomElements(List<T> list, int count) {
        if (list.size() <= count) {
            return new ArrayList<>(list);
        }
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, count);
    }

    private void trackAddRecommendSong(Track track, List<String> trackNames, List<RecommendSearch.RecommendSong> recommendSongs){
        trackNames.add(track.getName());
        List<RecommendSearch.RecommendArtist> artists = new ArrayList<>();
        artistArrToRecommendArtistList(track.getArtists(), artists);
        RecommendSearch.RecommendSong recommendSong = RecommendSearch.RecommendSong.of(
                track.getExternalUrls().get("spotify"),
                track.getName(),
                artists,
                track.getAlbum().getId(),
                track.getAlbum().getImages() != null && track.getAlbum().getImages().length > 0 ?
                        track.getAlbum().getImages()[0].getUrl() : null
        );
        recommendSongs.add(recommendSong);
    }
    public RecommendSearch getRecommendSearchValue(){
        Playlist test = spotifyDao.findMelonTop100();
        List<String> trackNames = new ArrayList<>();
        List<RecommendSearch.RecommendSong> recommendSongs = new ArrayList<>();
        for(PlaylistTrack playlistTrack : test.getTracks().getItems()){
            Track track = (Track) playlistTrack.getTrack();
            trackAddRecommendSong(track, trackNames, recommendSongs);
        }
        int topSize = Math.min(4, trackNames.size());
        List<String> top4TrackNames = new ArrayList<>(trackNames.subList(0, topSize));
        trackNames.subList(0, topSize).clear();
        int subSize = Math.min(6, trackNames.size());
        List<String> subTrackNames = getRandomElements(trackNames.subList(0, subSize), subSize);

        List<String> trackNameRes = Stream.concat(top4TrackNames.stream(), subTrackNames.stream()).toList();
        List<RecommendSearch.RecommendSong> artistRes = getRandomElements(recommendSongs, 10);

        return RecommendSearch.of(trackNameRes, artistRes);
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


