package com.github.musicsnsproject.repository.jpa.music.cart;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;

import java.util.Collection;
import java.util.List;

public interface MusicCartQueryRepository {


    // 로그인한 사용자 조회하기
    MyUser findMyUser(Long userId);

    // 장바구니에 담겨있는 개수 구해오기
    Long findMusicCartCount(MyUser user);

    // userId로 본인 musicCart 정보 조회하기
    List<MusicCart> findByCartUserId(Long userId);

    boolean cartTrackCheck(MyUser user, String trackId);

    boolean purchasedTrackCheck(MyUser user, String trackId);

    // cartId로 장바구니에 담긴 musicId 가져오기
    List<String> findMusicIdsByCartIds(List<Long> cartIdList);


}
