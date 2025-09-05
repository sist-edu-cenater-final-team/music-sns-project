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
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusic;
import com.github.musicsnsproject.repository.jpa.music.purchase.PurchaseMusicRepository;
import com.github.musicsnsproject.repository.spotify.SpotifyDao;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.musicsnsproject.repository.jpa.account.user.QMyUser.myUser;
import static com.github.musicsnsproject.repository.jpa.music.cart.QMusicCart.musicCart;
import static com.github.musicsnsproject.repository.jpa.music.purchase.QPurchaseMusic.purchaseMusic;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final MusicCartRepository musicCartRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final MyMusicRepository myMusicRepository;
    private final PurchaseMusicRepository purchaseMusicRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SpotifyDao spotifyDao;

    // 주문 미리보기
    @Override
    public List<CartResponse> getOrderPreviewList(Long userId, List<Long> cartIdList) {

        // userId로 본인 musicCart 정보 조회하기
        List<MusicCart> carts = musicCartRepository.findByCartUserId(userId);

        // 구매할 음악이 없을 경우
        if (cartIdList.isEmpty()) {
            throw CustomNotAcceptException.of()
                    .customMessage("구매할 음악이 없습니다.")
                    .request(cartIdList)
                    .build();
        }

        // 장바구니에 담겨있는 musicId 가져오기
        List<String> musicIds = carts.stream()
                .map(MusicCart::getMusicId)
                .toList();

        // 장바구니에 담겨있는 musicId들로 spotify Track 배열 조회하기
        Track[] tracks = spotifyDao.findAllTrackByIds(musicIds);

        // 조회한 Track 배열 Map으로 변환하기
        Map<String, Track> trackMap = Arrays.stream(tracks)
                .collect(Collectors.toMap(Track::getId, track -> track));



        List<CartResponse> cartResponseList = carts.stream()
                .filter(cart -> cartIdList.contains(cart.getMusicCartId()))
                .map(cart -> {

                    Track track = trackMap.get(cart.getMusicId());

                    String trackName = track.getName();
                    String albumName = track.getAlbum().getName();
                    String albumImageUrl = track.getAlbum().getImages()[0].getUrl();

                    String artistName = Arrays.stream(track.getArtists())
                            .map(ArtistSimplified::getName)
                            .distinct()
                            .collect(Collectors.joining(", "));


                    return CartResponse.builder()
                            .cartId(cart.getMusicCartId())
                            .userId(cart.getMyUser().getUserId())
                            .musicName(trackName)
                            .artistName(artistName)
                            .albumName(albumName)
                            .albumImageUrl(albumImageUrl)
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
        MyUser user = musicCartRepository.findMyUser(userId);

        long userCoin = user.getCoin();
        long orderTotalPrice = cartIdList.size() * 1;
        long totalPrice = userCoin - orderTotalPrice;

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


        List<String> musicIds = jpaQueryFactory
                .select(musicCart.musicId)
                .from(musicCart)
                .where(musicCart.musicCartId.in(cartIdList),
                       musicCart.myUser.userId.eq(userId))
                .fetch();

        // 구매한 음악에 추가하기
        List<PurchaseMusic> purchaseMusics = musicIds.stream()
                .map(musicIdList -> PurchaseMusic.builder()
                        .musicId(musicIdList)
                        .purchaseHistory(history)
                        .atThatCoin(userCoin)
                        .build()
                )
                .toList();

        purchaseMusicRepository.saveAll(purchaseMusics);

        // 내 음악에 추가하기
        MyMusic myMusic = MyMusic.builder()
                .purchaseHistory(history)
                .sourceType(MyMusicType.PURCHASE)
                .build();
        myMusicRepository.save(myMusic);


        // 주문이 끝난 음악들은 장바구니에서 삭제
        for (Long cardId : cartIdList) {
            musicCartRepository.deleteById(cardId);
        }
    }

    // 사용자의 보유 코인 알아오기
    @Override
    public void checkCoin(Long userId, List<Long> cartIdList) {

        // 사용자 조회
        MyUser user = musicCartRepository.findMyUser(userId);

        long userCoin = user.getCoin();
        long orderTotalPrice = cartIdList.size() * 1; // 주문에 필요한 음표

        if(userCoin < orderTotalPrice){
            throw CustomNotAcceptException.of()
                    .customMessage("보유 코인이 부족합니다. " +
                            "\n현재 보유 코인: " + userCoin +
                            "\n필요한 코인: " + orderTotalPrice)
                    .request(cartIdList)
                    .build();
        }
    }

    // 구매한적 있는 음악 체크하기
    @Override
    public void purchasedMusicCheck(Long userId, List<Long> cartIdList) {

        // cartId로 장바구니에 담긴 musicId 가져오기
        List<String> cartMusicIds = musicCartRepository.findMusicIdsByCartIds(cartIdList);

        // 구매헀던 음악 찾기
        List<String> purchasedMusicIds = purchaseMusicRepository.findPurchasedMusicIds(userId, cartMusicIds);

        if (!purchasedMusicIds.isEmpty()) {
            throw CustomNotAcceptException.of()
                    .customMessage("이미 구매한 음악이 있습니다: " + purchasedMusicIds)
                    .request(cartIdList)
                    .build();
        }
    }
}
