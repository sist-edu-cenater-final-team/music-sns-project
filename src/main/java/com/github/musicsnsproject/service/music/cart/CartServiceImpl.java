package com.github.musicsnsproject.service.music.cart;
import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.music.cart.MusicCart;
import com.github.musicsnsproject.repository.jpa.music.cart.MusicCartRepository;
import com.github.musicsnsproject.repository.spotify.SpotifyDao;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.musicsnsproject.repository.jpa.music.cart.QMusicCart.musicCart;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final MusicCartRepository musicCartRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final SpotifyDao spotifyDao;


    // 장바구니 리스트
    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> getCartList(Long userId) {

        // userId로 본인 musicCart 정보 조회하기
        List<MusicCart> carts = musicCartRepository.findByCartUserId(userId);

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
                            .musicId(cart.getMusicId())
                            .musicName(trackName)
                            .artistId(track.getArtists()[0].getId())
                            .artistName(artistName)
                            .albumId(track.getAlbum().getId())
                            .albumName(albumName)
                            .albumImageUrl(albumImageUrl)
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
        MyUser user = musicCartRepository.findMyUser(userId);

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
        boolean cartTrackCheck = musicCartRepository.cartTrackCheck(user, trackId);
        // 중복된 음악이 장바구니에 있는 경우 예외 처리
        if (cartTrackCheck) {
            throw CustomNotAcceptException.of()
                    .customMessage("이미 장바구니에 담긴 음악입니다.")
                    .request(trackId)
                    .build();
        }
        // 구매내역에 있는 음악인지 확인하기
        boolean purchasedTrackCheck = musicCartRepository.purchasedTrackCheck(user, trackId);

        if(purchasedTrackCheck) {
            throw CustomNotAcceptException.of()
                    .customMessage("이미 구매했던 음악입니다.")
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
        MyUser user = musicCartRepository.findMyUser(userId);

        if(user != null) {
            for (Long cartId : cartIdList) {
                musicCartRepository.deleteById(cartId);
            }
        }
    }
}
