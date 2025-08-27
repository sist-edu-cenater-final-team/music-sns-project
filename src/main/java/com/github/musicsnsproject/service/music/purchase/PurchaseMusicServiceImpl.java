package com.github.musicsnsproject.service.music.purchase;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.music.MyMusic;
import com.github.musicsnsproject.repository.jpa.music.cart.MusicCart;
import com.github.musicsnsproject.repository.jpa.music.cart.MusicCartRepository;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseHistory;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusic;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusicRepository;
import com.github.musicsnsproject.repository.spotify.SpotifyDao;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import com.github.musicsnsproject.web.dto.music.purchase.PurchaseMusicResponse;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.cart.QMusicCart.musicCart;
import static com.github.musicsnsproject.repository.jpa.music.QMyMusic.myMusic;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic.purchaseMusic;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseHistory.purchaseHistory;

@Service
@RequiredArgsConstructor

public class PurchaseMusicServiceImpl implements PurchaseMusicService {

    private final MusicCartRepository musicCartRepository;
    private final PurchaseMusicRepository purchaseMusicRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SpotifyDao spotifyDao;

    @Override
    public List<PurchaseMusicResponse> getPurchaseMusicList(Long userId) {

        // userId로 본인 PurchaseMusic 정보 조회하기
        List<PurchaseMusic> purchaseMusics = purchaseMusicRepository.findByPurchaseMusicUserId(userId);


        // 구매내역 id 가져오기
//        List<PurchaseHistory> purchaseHistoryIds = purchaseMusics.stream()
//                .map(PurchaseMusic::getPurchaseHistory)
//                .toList();

        // 구매내역 id로 포함되어있는 musicId 가져오기
        List<String> musicIds = purchaseMusics.stream()
                .map(PurchaseMusic::getMusicId)
                .toList();

        Track[] tracks = spotifyDao.findAllTrackByIds(musicIds);

        // 조회한 Track 배열 Map으로 변환하기
        Map<String, Track> trackMap = Arrays.stream(tracks)
                .collect(Collectors.toMap(Track::getId, track -> track));

        // 로그인한 유저의 구매한 음악 조회하기
        List<PurchaseMusicResponse> purchaseMusicResponseList = purchaseMusics.stream()
                .map(pm -> {

                    Track track = trackMap.get(pm.getMusicId());

                    String trackName = track.getName();
                    String albumName = track.getAlbum().getName();
                    String albumImageUrl = track.getAlbum().getImages()[0].getUrl();

                    String artistName = Arrays.stream(track.getArtists())
                            .map(ArtistSimplified::getName)
                            .distinct()
                            .collect(Collectors.joining(", "));


                    return PurchaseMusicResponse.builder()
                            .musicId(pm.getMusicId())
                            .musicName(trackName)
                            .artistName(artistName)
                            .albumName(albumName)
                            .albumImageUrl(albumImageUrl)
                            .purchaseHistoryId(pm.getPurchaseHistory().getPurchaseHistoryId())
                            .build();


                })
                .toList();


        return purchaseMusicResponseList;
    }
}
