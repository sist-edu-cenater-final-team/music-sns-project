package com.github.musicsnsproject.service.music.order;

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
public class OrderServiceImpl implements OrderService{

    private final MusicCartRepository musicCartRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final MyMusicRepository myMusicRepository;
    private final SpotifyMusicService spotifyMusicService;
    private final JPAQueryFactory jpaQueryFactory;

    // 주문 미리보기
    @Override
    public List<CartResponse> getOrderPreviewList(Long userId, List<Long> cartIdList) {

        List<MusicCart> carts = jpaQueryFactory
                .selectFrom(musicCart)
                .join(musicCart.myUser, myUser).fetchJoin()
                .where(musicCart.myUser.userId.eq(userId))
                .orderBy(musicCart.createdAt.desc())
                .fetch();

        // 구매할 음악이 없을 경우
        if (cartIdList.isEmpty()) {
            throw CustomNotAcceptException.of()
                    .customMessage("구매할 음악이 없습니다.")
                    .request(cartIdList)
                    .build();
        }

        List<CartResponse> cartResponseList = carts.stream()
                .filter(cart -> cartIdList.contains(cart.getMusicCartId()))
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

    // 주문 확정하기
    @Override
    @Transactional
    public void orderConfirm(Long userId, List<Long> cartIdList) {
        // 사용자 조회
        MyUser user = findMyUserFetchJoin(userId);

        long userCoin = user.getCoin();
        long orderTotalPrice = cartIdList.size() * 1; // 장바구니에 담겨있는 음악의 개수만큼 1코인씩 차감

        long totalPrice = userCoin - orderTotalPrice;

        if(userCoin < orderTotalPrice){
            throw CustomNotAcceptException.of()
                    .customMessage("보유 코인이 부족합니다. 현재 보유 코인: " + userCoin +
                            ", 필요한 코인: " + orderTotalPrice)
                    .request(cartIdList)
                    .build();
        }

        // 구매내역 추가하기
        PurchaseHistory history = PurchaseHistory.builder()
                .myUser(user)
                .purchasedAt(LocalDateTime.now())
                .atThatUserCoin(userCoin)
                .build();

        // 코인 차감
        jpaQueryFactory.update(myUser)
                .set(myUser.coin, totalPrice)
                .where(myUser.userId.eq(userId))
                .execute();

        // 구매내역 저장
        purchaseHistoryRepository.save(history);


        // 내 음악에 추가하기
        MyMusic myMusic = MyMusic.builder()
                .purchaseHistory(history)
                .sourceType(MyMusicType.PURCHASE)
                .build();

        // 내 음악 저장
        myMusicRepository.save(myMusic);

        // 장바구니에서 삭제
        for (Long cardId : cartIdList) {
            musicCartRepository.deleteById(cardId);
        }
    }


    // 사용자 조회 공통
    public MyUser findMyUserFetchJoin(Long userId) {

        if (userId == null) {
            throw CustomNotAcceptException.of()
                    .customMessage("사용자를 찾을 수 없습니다.")
                    .request(userId)
                    .build();
        }
        // 사용자가 있으면
        return jpaQueryFactory.selectFrom(myUser)
                .where(myUser.userId.eq(userId))
                .fetchOne();
    }
}
