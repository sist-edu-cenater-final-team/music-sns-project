package com.github.musicsnsproject.repository.jpa.music.cart;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;

import java.util.List;

public interface MusicCartQueryRepository {


    // 로그인한 사용자 조회하기
    MyUser findMyUser(Long userId);

    // userId로 본인 musicCart 정보 조회하기
    List<MusicCart> findByCartUserId(Long userId);

    boolean cartTrackCheck(MyUser user, String trackId);

    boolean purchasedTrackCheck(MyUser user, String trackId);
}
