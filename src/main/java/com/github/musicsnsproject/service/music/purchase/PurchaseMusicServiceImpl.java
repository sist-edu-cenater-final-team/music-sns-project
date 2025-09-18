package com.github.musicsnsproject.service.music.purchase;

import com.github.musicsnsproject.domain.purchase.PurchaseMusicVO;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusic;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusicRepository;
import com.github.musicsnsproject.repository.spotify.SpotifyRepository;
import com.github.musicsnsproject.web.dto.music.purchase.PurchaseMusicResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseMusicServiceImpl implements PurchaseMusicService {

    private final PurchaseMusicRepository purchaseMusicRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SpotifyRepository spotifyRepository;

    @Override
    public PurchaseMusicResponse getPurchaseMusicList(Long userId, int currentShowPageNo) {

        int sizePerPage = 15;         // 한 페이지당 보여줄 게시글 건수

        int pageIndex = currentShowPageNo-1;
        // 페이징 보여줄 개수
        int blockSize = 5;

        // 페이지 번호
        int pageNo = (pageIndex/blockSize) * blockSize + 1;

        Pageable pageable = PageRequest.of(pageIndex, sizePerPage, Sort.by(Sort.Direction.DESC, "purchaseMusicId"));

        Page<PurchaseMusic> pagePurchaseMusic = purchaseMusicRepository.findPageByPurchaseMusicUserId(userId, pageable);

        List<PurchaseMusic> purchaseMusics = pagePurchaseMusic.getContent();

        // 전체 페이지 개수
        int totalPage = pagePurchaseMusic.getTotalPages();
        long totalElements = pagePurchaseMusic.getTotalElements();

        // userId로 본인 PurchaseMusic 정보 조회하기
        //List<PurchaseMusic> purchaseMusics = purchaseMusicRepository.findByPurchaseMusicUserId(userId);

        // musicId 가져오기
        List<String> musicIds = purchaseMusics.stream()
                .map(PurchaseMusic::getMusicId)
                .toList();

        Track[] tracks = spotifyRepository.findAllTrackByIds(musicIds);

        // 조회한 Track 배열 Map으로 변환하기
        Map<String, Track> trackMap = Arrays.stream(tracks)
                .collect(Collectors.toMap(Track::getId, track -> track));



        // 로그인한 유저의 구매한 음악 조회하기
        List<PurchaseMusicVO> musicList = purchaseMusics.stream().map(pm -> {

            Track track = trackMap.get(pm.getMusicId());

            // 트랙의 아티스트 목록에서 중복 제외한 이름을 가져오고 문자열로 만들어버리기
            String artistName = Arrays.stream(track.getArtists())
                    .map(ArtistSimplified::getName)
                    .distinct()
                    .collect(Collectors.joining(", "));

            return PurchaseMusicVO.builder()
                    .purchaseMusicId(pm.getPurchaseMusicId())
                    .musicId(pm.getMusicId())
                    .musicName(track.getName())
                    .artistId(track.getArtists()[0].getId())
                    .artistName(artistName)
                    .albumId(track.getAlbum().getId())
                    .albumName(track.getAlbum().getName())
                    .albumImageUrl(track.getAlbum().getImages()[0].getUrl())
                    .purchaseHistoryId(pm.getPurchaseHistory().getPurchaseHistoryId())
                    .build();
        }).toList();

        return PurchaseMusicResponse.builder()
                .purchaseMusic(musicList)
                .pageNo(currentShowPageNo)
                .pageSize(sizePerPage)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();
    }
}
