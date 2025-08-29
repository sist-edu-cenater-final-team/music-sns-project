package com.github.musicsnsproject.repository.jpa.account.follow;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.musicsnsproject.domain.user.MyUserVO;

public interface FollowRepository extends JpaRepository<Follow, FollowPk>, FollowQueryRepository {

    // 팔로우 여부 확인
    boolean existsByFollowPk(FollowPk followPk);

    // 팔로우 취소
    void deleteByFollowPk(FollowPk followPk);

    // 팔로우 수 조회
    long countByFollowPk_Followee_UserId(long followeeId);
    // 팔로워 수 조회
    long countByFollowPk_Follower_UserId(long followerId);

    
	


	



}
