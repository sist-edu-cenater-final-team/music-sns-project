package com.github.musicsnsproject.repository.jpa.account.follow;

import java.util.List;
import java.util.Map;

import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.account.user.QMyUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FollowQueryRepositoryImpl implements FollowQueryRepository {
    private final JPAQueryFactory queryFactory;

	@Override
	public List<FollowVO> findByFollowerAndUserInfo(String userId) {
	    QFollow follow = QFollow.follow;
	    QMyUser user = QMyUser.myUser;  
	    QFollow follow2 = new QFollow("follow2");


	    return queryFactory.select(Projections.constructor(FollowVO.class,
	            follow.followPk.followee.userId,   // 친구 ID
	            follow.followPk.follower.userId,   // 내 ID
	            Projections.constructor(MyUserVO.class,
	                    user.userId,
	                    user.nickname,
	                    user.email,
	                    user.profileImage,
	                    user.profileMessage
	            ),
	            new CaseBuilder()
                .when(follow2.isNotNull())
                .then(true)
                .otherwise(false)
                .as("teist")
	        ))
	        .from(follow)
	        .join(user).on(follow.followPk.followee.userId.eq(user.userId))
	        .leftJoin(follow2)
	        .on(follow2.followPk.follower.userId.eq(Long.parseLong(userId))
	        	.and(follow2.followPk.followee.userId.eq(follow.followPk.follower.userId)))
	        .where(follow.followPk.follower.userId.eq(Long.parseLong(userId)))
	        .fetch();
	}

	@Override
	public List<FollowVO> findByFolloweeAndUserInfo(String userId) {
		
		QFollow follow = QFollow.follow;
		QFollow follow2 = new QFollow("follow2");
		QMyUser user = QMyUser.myUser;
		
	    return queryFactory.select(Projections.constructor(FollowVO.class,
	            follow.followPk.followee.userId,   // 친구 ID
	            follow.followPk.follower.userId,   // 내 ID
	            Projections.constructor(MyUserVO.class,
	                    user.userId,
	                    user.nickname,
	                    user.email,
	                    user.profileImage,
	                    user.profileMessage
	            ),
	            // follow2가 존재하면 true, 없으면 false
	            new CaseBuilder()
	                .when(follow2.isNotNull())
	                .then(true)
	                .otherwise(false)
	                .as("teist")

	        ))
	        .from(follow)
	        .join(user)
	        .on(follow.followPk.follower.userId.eq(user.userId))

	        .leftJoin(follow2)
	        .on(follow2.followPk.follower.userId.eq(Long.parseLong(userId))
	        	.and(follow2.followPk.followee.userId.eq(follow.followPk.follower.userId)))
	        .where(follow.followPk.followee.userId.eq(Long.parseLong(userId)))
	        .fetch();
	}

	@Override
	public List<FollowVO> findCommonFriend(String userId) {

		QFollow follow1 = new QFollow("follow1"); // 나를 팔로우하는 사람
		QFollow follow2 = new QFollow("follow2"); // 그 사람이 팔로우하는 사람
		QMyUser user  = QMyUser.myUser;    // 회원 엔티티

		return queryFactory
		    .select(Projections.constructor(FollowVO.class, 
		        follow1.followPk.follower.userId.countDistinct().as("teistCount")
		    	,
		    	Projections.constructor(MyUserVO.class,
		                user.userId,
		                user.nickname,
		                user.email,
		                user.profileImage,
		                user.profileMessage
		         )))
		    .from(follow1)
		    .join(follow2).on(follow1.followPk.follower.eq(follow2.followPk.follower))
		    .join(user).on(follow2.followPk.followee.eq(user))
		    .where(
		        follow1.followPk.followee.userId.eq(Long.parseLong(userId)), // 나를 팔로우하는 사람
		        follow2.followPk.followee.userId.ne(Long.parseLong(userId)), // 자기 자신 제외
		        follow2.followPk.followee.userId.notIn(
		            JPAExpressions // 쿼리dsl 에서 서브쿼리
		                .select(QFollow.follow.followPk.followee.userId)
		                .from(QFollow.follow)
		                .where(QFollow.follow.followPk.follower.userId.eq(Long.parseLong(userId)))
		                // 내가 이미 팔로우한 사람 제외
		        )
		    )
		    .groupBy(user.userId,
		    	    user.nickname,
		    	    user.email,
		    	    user.profileImage,
		    	    user.profileMessage)
		    .orderBy(follow1.followPk.follower.userId.countDistinct().desc())
		    .fetch();
	}

	@Override
	@Transactional
	public int addFollow(Map<String, String> map) {
		QFollow follow = QFollow.follow;

		queryFactory.insert(follow)
						.columns(follow.followPk.followee, follow.followPk.follower)
						.values(
								map.get("followee"),
								map.get("follower"))
						.execute();
		return 1;
	}

	@Override
	public List<FollowVO> searchUser(String searchWord, String userId) {

		QMyUser user = QMyUser.myUser;
		QFollow follow = QFollow.follow;

	    BooleanBuilder builder = new BooleanBuilder();

	    if (searchWord != null && !searchWord.isEmpty()) {
	        builder.and(
	            user.nickname.containsIgnoreCase(searchWord)
	                .or(user.email.containsIgnoreCase(searchWord))
	        );
	    }
	    return queryFactory
	            .select(Projections.constructor(FollowVO.class,

	                user.userId,

	                Expressions.constant(Long.parseLong(userId)),
	                // MyUserVO 생성
	                Projections.constructor(MyUserVO.class,
	                    user.userId,
	                    user.nickname,
	                    user.email,
	                    user.profileImage,
	                    user.profileMessage
	                ),
	                // teist (팔로우 여부)
	                new CaseBuilder()
	                    .when(follow.isNotNull())
	                    .then(true)
	                    .otherwise(false)
	            ))
	            .from(user)
	            .leftJoin(follow)
	                .on(follow.followPk.follower.userId.eq(Long.parseLong(userId))
	                    .and(follow.followPk.followee.userId.eq(user.userId)))
	            .where(builder)
	            .fetch();
	}





}
