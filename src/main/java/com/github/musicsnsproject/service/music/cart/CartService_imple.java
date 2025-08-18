package com.github.musicsnsproject.service.music.cart;

import com.github.musicsnsproject.common.exceptions.CustomNotFoundException;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.music.cart.MusicCart;
import com.github.musicsnsproject.repository.jpa.music.cart.MusicCartRepository;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.advice.ExceptionControllerAdvice;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackArtist;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.cart.QMusicCart.musicCart;

@Service
@RequiredArgsConstructor
public class CartService_imple implements CartService {

    private final MusicCartRepository musicCartRepository;
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
                    TrackResponse tr = spotifyMusicService.getTrackResponseById(cart.getMusicId());

                    String albumName = (tr != null && tr.album() != null) ? tr.album().albumName() : null;
                    String albumImageUrl = (tr != null && tr.album() != null) ? tr.album().albumImageUrl() : null;
                    String artistName = (tr != null && tr.artist() != null)
                            ? tr.artist().stream()
                            .map(TrackArtist::artistName)
                            .distinct()
                            .collect(java.util.stream.Collectors.joining(", "))
                            : null;

                    return CartResponse.builder()
                            .cartId(cart.getMusicCartId())
                            .userId(cart.getMyUser().getUserId())
                            .musicId(cart.getMusicId())
                            .musicName(tr != null ? tr.trackName() : null)
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

        // 중복 여부 확인
        boolean exists = jpaQueryFactory.selectFrom(musicCart)
                .where(musicCart.myUser.eq(user).and(musicCart.musicId.eq(trackId)))
                .fetchFirst() != null;

        // 중복이 아니면 저장
        if (!exists) {
            musicCartRepository.save(
                    MusicCart.builder()
                            .musicId(trackId)
                            .myUser(user)
                            .build()
            );
        }

        // 업데이트 된 장바구니 반환
        return getCartList(userId);

    }

    // 장바구니 삭제하기
    @Override
    public void deleteCart(Long userId, List<Long> cartIdList) {

        // 사용자 조회
        MyUser user = findMyUserFetchJoin(userId);

        if (user != null) {
            for (Long cartId : cartIdList){
                musicCartRepository.deleteById(cartId);
            }

        }
    }

    // 사용자 조회 공통
    public MyUser findMyUserFetchJoin(Long userId) {
        return jpaQueryFactory.selectFrom(myUser)
                .where(myUser.userId.eq(userId))
                .fetchOne();
    }
}
