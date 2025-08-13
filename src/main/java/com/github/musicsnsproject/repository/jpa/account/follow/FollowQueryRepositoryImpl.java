package com.github.musicsnsproject.repository.jpa.account.follow;

<<<<<<< HEAD
import java.util.List;

import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.account.user.QMyUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FollowQueryRepositoryImpl implements FollowQueryRepository {
    private final JPAQueryFactory queryFactory;

	@Override
	public List<FollowVO> findByFollowerAndUserInfo(String userId) {
	    QFollow follow = QFollow.follow;
	    QMyUser user = QMyUser.myUser;  

	    return queryFactory.select(Projections.constructor(FollowVO.class,
	            follow.followPk.followee.userId,   // 친구 ID
	            follow.followPk.follower.userId,   // 내 ID
	            Projections.constructor(MyUserVO.class,
	                    user.userId,
	                    user.nickname,
	                    user.profileImage,
	                    user.email
	            )
	        ))
	        .from(follow)
	        .join(user).on(follow.followPk.followee.userId.eq(user.userId))
	        .where(follow.followPk.follower.userId.eq(Long.parseLong(userId)))
	        .fetch();
	}

	@Override
	public List<FollowVO> findByFolloweeAndUserInfo(String userId) {
		
		QFollow follow = QFollow.follow;
		QMyUser user = QMyUser.myUser;
		
	    return queryFactory.select(Projections.constructor(FollowVO.class,
	            follow.followPk.followee.userId,   // 친구 ID
	            follow.followPk.follower.userId,   // 내 ID
	            Projections.constructor(MyUserVO.class,
	                    user.userId,
	                    user.nickname,
	                    user.profileImage,
	                    user.email
	            )
	        ))
	        .from(follow)
	        .join(user).on(follow.followPk.follower.userId.eq(user.userId))
	        .where(follow.followPk.followee.userId.eq(Long.parseLong(userId)))
	        .fetch();
	}

	@Override
	public List<FollowVO> findCommonFriend(String userId) {

		QFollow f1 = new QFollow("f1"); // 나를 팔로우하는 사람
		QFollow f2 = new QFollow("f2"); // 그 사람이 팔로우하는 사람
		QMyUser m  = QMyUser.myUser;    // 회원 엔티티

		return queryFactory
		    .select(Projections.constructor(FollowVO.class, 
		        m.userId,
		        m.nickname,
		        f1.followPk.follower.userId.countDistinct() // mutualCount
		    ))
		    .from(f1) 
		    .join(f2).on(f1.followPk.follower.eq(f2.followPk.follower))
		    .join(m).on(f2.followPk.followee.eq(m))
		    .where(
		        f1.followPk.followee.userId.eq(Long.parseLong(userId)), // 나를 팔로우하는 사람
		        f2.followPk.followee.userId.ne(Long.parseLong(userId)), // 자기 자신 제외
		        f2.followPk.followee.userId.notIn(
		            JPAExpressions
		                .select(QFollow.follow.followPk.followee.userId)
		                .from(QFollow.follow)
		                .where(QFollow.follow.followPk.follower.userId.eq(Long.parseLong(userId))) // 내가 이미 팔로우한 사람 제외
		        )
		    )
		    .groupBy(m.userId, m.nickname)
		    .orderBy(f1.followPk.follower.userId.countDistinct().desc())
		    .fetch();
	}
    
=======
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FollowQueryRepositoryImpl implements FollowQueryRepository {
    private final JPAQueryFactory queryFactory;
>>>>>>> branch 'main' of https://github.com/sist-edu-cenater-final-team/music-sns-project.git
}
