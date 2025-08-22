package com.github.musicsnsproject.service.music.cart;
import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.common.myenum.MyMusicType;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.music.MyMusic;
import com.github.musicsnsproject.repository.jpa.music.MyMusicRepository;
import com.github.musicsnsproject.repository.jpa.music.cart.MusicCart;
import com.github.musicsnsproject.repository.jpa.music.cart.MusicCartRepository;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseHistory;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseHistoryRepository;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import com.github.musicsnsproject.web.dto.music.spotify.artist.SimplifiedArtist;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponseV1;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.cart.QMusicCart.musicCart;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final MusicCartRepository musicCartRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final MyMusicRepository myMusicRepository;
    private final SpotifyMusicService spotifyMusicService;
    private final JPAQueryFactory jpaQueryFactory;


    // 장바구니 리스트
    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> getCartList(Long userId) {

        List<MusicCart> carts = jpaQueryFactory
                .selectFrom(musicCart)
                .join(musicCart.myUser, myUser).fetchJoin()
                .where(musicCart.myUser.userId.eq(userId))
                .orderBy(musicCart.createdAt.desc())
                .fetch();

        List<CartResponse> cartResponseList = carts.stream()
                .map(cart -> {
                    TrackResponseV1 tr = spotifyMusicService.getTrackResponseById(cart.getMusicId());

                    String albumName = (tr != null && tr.getAlbum() != null) ? tr.getAlbum().getAlbumName() : null;
                    String albumImageUrl = (tr != null && tr.getAlbum() != null) ? tr.getAlbum().getAlbumImageUrl() : null;
                    String artistName = (tr != null && tr.getArtist() != null)
                            ? tr.getArtist().stream()
                            .map(SimplifiedArtist::artistName)
                            .distinct()
                            .collect(java.util.stream.Collectors.joining(", "))
                            : null;

                    return CartResponse.builder()
                            .cartId(cart.getMusicCartId())
                            .userId(cart.getMyUser().getUserId())
                            .musicId(cart.getMusicId())
                            .musicName(tr != null ? tr.getTrackName() : null)
                            .albumName(albumName)
                            .albumImageUrl(albumImageUrl)
                            .artistName(artistName)
                            .build();
                })
                .toList();

        return cartResponseList;
    }

    // 장바구니 담기
    @Override
    @Transactional
    public List<CartResponse> addCart(Long userId, String trackId) {

        // 사용자 조회
        MyUser user = findMyUserFetchJoin(userId);

        // 장바구니에 담긴 음악 개수 구하기
        Long count = jpaQueryFactory
                .select(musicCart.count())
                .from(musicCart)
                .where(musicCart.myUser.eq(user))
                .fetchOne();

        long cartCount = count == null ? 0L : count;

        // 장바구니에 담긴 음악 개수가 20개 이상인 경우 예외 처리
        if(cartCount >= 20){
            throw CustomNotAcceptException.of()
                    .customMessage("장바구니에 담을 수 있는 음악의 개수는 최대 20개입니다.")
                    .request(trackId)
                    .build();
        }

        // 장바구니에 같은 음악이 있는지 확인하기
        boolean hasTrack = jpaQueryFactory
                .selectOne()
                .from(musicCart)
                .where(musicCart.myUser.eq(user)
                        .and(musicCart.musicId.eq(trackId)))
                .fetchFirst() != null;

        if (hasTrack) {
            // 중복된 음악이 장바구니에 있는 경우 예외 처리
            throw CustomNotAcceptException.of()
                    .customMessage("이미 장바구니에 담긴 음악입니다.")
                    .request(trackId)
                    .build();
        }

        // 음악이 중복이 아니면 저장
        musicCartRepository.save(
                MusicCart.builder()
                .musicId(trackId)
                .myUser(user)
                .build()
        );

        // 업데이트 된 장바구니 반환
        return getCartList(userId);
    }

    // 장바구니 삭제하기
    @Override
    public void deleteCart(Long userId, List<Long> cartIdList) {

        // 사용자 조회
        MyUser user = findMyUserFetchJoin(userId);
        if (user == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("사용자를 찾을 수 없습니다.")
                    .request(userId)
                    .build();
        }

        for (Long cartId : cartIdList) {
            musicCartRepository.deleteById(cartId);
        }
    }

    // 사용자 조회 공통
    public MyUser findMyUserFetchJoin(Long userId) {
        return jpaQueryFactory.selectFrom(myUser)
                .where(myUser.userId.eq(userId))
                .fetchOne();
    }
}
