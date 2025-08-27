package com.github.musicsnsproject.repository.jpa.account.follow;

import java.util.List;
import java.util.Map;

import org.eclipse.tags.shaded.org.apache.bcel.generic.Select;

import com.github.musicsnsproject.domain.follow.FollowVO;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.account.user.QMyUser;
import com.github.musicsnsproject.repository.jpa.community.block.QBlockUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
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
	public List<FollowVO> findByFollowerAndUserInfo(Long userId) {
	    QFollow follow = QFollow.follow;
	    QMyUser user = QMyUser.myUser;  
	    QFollow follow2 = new QFollow("follow2");
	    QBlockUser blockUser = QBlockUser.blockUser;

	    return queryFactory.select(Projections.fields(FollowVO.class,
	            follow.followPk.followee.userId.as("followee"),
	            follow.followPk.follower.userId.as("follower"),
	            Projections.fields(MyUserVO.class,
	                    user.userId,
	                    user.nickname,
	                    user.email,
	                    user.profileImage.as("profile_image"),
	                    user.profileMessage
			            ).as("user"),
			    		ExpressionUtils.as(
			    			    JPAExpressions
			    			        .selectOne()
			    			        .from(follow)
			    			        .where(
			    			            follow.followPk.follower.userId.eq(userId)
			    			            .and(follow.followPk.followee.userId.eq(user.userId))
			    			            .and(follow.favorite.isTrue())
			    			        )
			    			        .exists(),
			    			    "favorite"
			    			),
			            new CaseBuilder()
			                .when(follow2.isNotNull())
			                .then(true)
			                .otherwise(false)
			                .as("teist")
					    )

			    		)
					    .from(follow)
					    .join(user).on(follow.followPk.followee.userId.eq(user.userId))
					    .leftJoin(follow2)
					        .on(follow2.followPk.follower.userId.eq(userId)
					            .and(follow2.followPk.followee.userId.eq(follow.followPk.follower.userId)))
					    .leftJoin(blockUser)
					        .on(blockUser.blockUserPk.myUser.userId.eq(userId)
					            .and(blockUser.blockUserPk.blockUser.userId.eq(user.userId)))
					    .where(
					        follow.followPk.follower.userId.eq(userId)
					        .and(blockUser.blockUserPk.myUser.isNull()) 
					    )
					    .fetch();
			}

	@Override
	public List<FollowVO> findByFolloweeAndUserInfo(Long userId) {
		
		QFollow follow = QFollow.follow;
		QFollow follow2 = new QFollow("follow2");
		QMyUser user = QMyUser.myUser;
		QBlockUser blockUser = QBlockUser.blockUser;
	    return queryFactory.select(Projections.fields(FollowVO.class,
	            follow.followPk.followee.userId,   // 친구 ID
	            follow.followPk.follower.userId,   // 내 ID
	            Projections.fields(MyUserVO.class,
	                    user.userId,
	                    user.nickname,
	                    user.email,
	                    user.profileImage.as("profile_image"),
	                    user.profileMessage
	            ).as("user"),
	            ExpressionUtils.as(
	    			    JPAExpressions
	    			        .selectOne()
	    			        .from(follow)
	    			        .where(
	    			            follow.followPk.follower.userId.eq(userId)
	    			            .and(follow.followPk.followee.userId.eq(user.userId))
	    			            .and(follow.favorite.isTrue())
	    			        )
	    			        .exists(),
	    			    "favorite"
	    			),
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
	        .on(follow2.followPk.follower.userId.eq(userId)
	        	.and(follow2.followPk.followee.userId.eq(follow.followPk.follower.userId)))
		    .leftJoin(blockUser)
	        .on(blockUser.blockUserPk.myUser.userId.eq(userId)
	            .and(blockUser.blockUserPk.blockUser.userId.eq(user.userId)))
	        .where(follow.followPk.followee.userId.eq(userId)
	        		.and(blockUser.blockUserPk.myUser.isNull()))
	        .fetch();
	}

	@Override
	public List<FollowVO> findCommonFriend(Long userId) {

		QFollow follow1 = new QFollow("follow1"); // 나를 팔로우하는 사람
		QFollow follow2 = new QFollow("follow2"); // 그 사람이 팔로우하는 사람
		QMyUser user  = QMyUser.myUser;    // 회원 엔티티

		return queryFactory
		    .select(Projections.constructor(FollowVO.class, 
		        follow1.followPk.follower.userId.countDistinct().as("teistCount"),
		        follow1.followPk.follower.userId
		    	,
		    	Projections.constructor(MyUserVO.class,
		                user.userId,
		                user.nickname,
		                user.email,
		                user.profileImage,
		                user.profileMessage
		         )))
		    .from(follow1)
		    .join(follow2)
		    .on(follow1.followPk.follower.eq(follow2.followPk.follower))
		    .join(user)
		    .on(follow2.followPk.followee.eq(user))
		    .where(
		        follow1.followPk.followee.userId.eq(userId), // 나를 팔로우하는 사람
		        follow2.followPk.followee.userId.ne(userId), // 자기 자신 제외
		        follow2.followPk.followee.userId.notIn(
		            JPAExpressions // 쿼리dsl 에서 서브쿼리
		                .select(QFollow.follow.followPk.followee.userId)
		                .from(QFollow.follow)
		                .where(QFollow.follow.followPk.follower.userId.eq(userId))
		                // 내가 이미 팔로우한 사람 제외
		        )
		    )
		    .groupBy(follow1.followPk.follower.userId,
		    	    user.userId,
		    	    user.nickname,
		    	    user.email,
		    	    user.profileImage,
		    	    user.profileMessage)
		    .orderBy(follow1.followPk.follower.userId.countDistinct().desc())
		    .fetch();
	}

	@Override
	@Transactional
	public int addFollow(Map<String, Long> map) {
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
	public List<FollowVO> searchUser(String searchWord, Long userId) {

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

	                Expressions.constant(userId),     
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
	                .on(follow.followPk.follower.userId.eq(userId)
	                    .and(follow.followPk.followee.userId.eq(user.userId)))
	            .where(builder.and(follow.followPk.follower.userId.eq(userId)))
	            .fetch();
	}

	@Override
	@Transactional
	public long unFollow(Map<String, Long> map) {
		QFollow follow = QFollow.follow;
		return queryFactory
		        .delete(follow)
		        .where(
		            follow.followPk.follower.userId.eq(map.get("follower"))
		            .and(follow.followPk.followee.userId.eq(map.get("followee")))
		        )
		        .execute();
	}

	@Override
	public List<FollowVO> getfavoriteList(Long userId) {
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
	        .on(follow2.followPk.follower.userId.eq(userId)
	        	.and(follow2.followPk.followee.userId.eq(follow.followPk.follower.userId)))
	        .where(follow.followPk.follower.userId.eq(userId)
	        		.and(follow.favorite.isTrue()))
	        .fetch();
	}

	@Override
	@Transactional
	public long unFavorite(Map<String, Long> map) {
	    QFollow follow = QFollow.follow;

	    return queryFactory
	            .update(follow)
	            .set(follow.favorite, false)
	            .where(follow.followPk.follower.userId.eq(map.get("follower"))
	                   .and(follow.followPk.followee.userId.eq(map.get("followee"))))
	            .execute();  
	}

	@Override
	@Transactional
	public long addFavorite(Map<String, Long> map) {
		QFollow follow = QFollow.follow;
		
		
		return queryFactory.update(follow)
						.set(follow.favorite, true)
							.where(follow.followPk.follower.userId.eq(map.get("follower"))
									.and(follow.followPk.followee.userId.eq(map.get("followee"))))
							.execute();
	}

	@Override
	@Transactional
	public long addBlock(Map<String, Long> map) {
		QBlockUser user = QBlockUser.blockUser;
		
		return queryFactory.insert(user)
							.columns(user.blockUserPk.myUser, user.blockUserPk.blockUser)
							.values(map.get("userId"),map.get("blockUser"))
							.execute();
	}

	@Override
	public Long followeeCount(Long userId) {
		QFollow follow = QFollow.follow;
		QBlockUser blockUser = QBlockUser.blockUser;
		QMyUser user = QMyUser.myUser;

		return queryFactory
			    .select(follow.count())
			    .from(follow)
			    .join(user).on(follow.followPk.follower.userId.eq(user.userId))
			    .leftJoin(blockUser)
			        .on(blockUser.blockUserPk.myUser.userId.eq(userId)
			            .and(blockUser.blockUserPk.blockUser.userId.eq(user.userId)))
			    .where(
			        follow.followPk.followee.userId.eq(userId), // 나를 팔로우하는 사람
			        blockUser.blockUserPk.myUser.isNull()       // 차단 안 된 유저만
			    )
			    .fetchOne();
	}

	@Override
	public Long followerCount(Long userId) {
		QFollow follow = QFollow.follow;
		QBlockUser blockUser = QBlockUser.blockUser;
		QMyUser user = QMyUser.myUser;
		
		return queryFactory
		        .select(follow.count())
		        .from(follow)
		        .join(user).on(follow.followPk.follower.userId.eq(user.userId)) 
		        .leftJoin(blockUser)
		            .on(blockUser.blockUserPk.myUser.userId.eq(userId)
		                .and(blockUser.blockUserPk.blockUser.userId.eq(user.userId))) 
		        .where(
		            follow.followPk.followee.userId.eq(userId), 
		            blockUser.blockUserPk.myUser.isNull()       
		        )
		        .fetchOne();
	}

	@Override
	public Long favoriteCount(Long userId) {
		QFollow follow = QFollow.follow;
		QBlockUser blockUser = QBlockUser.blockUser;
		QMyUser user = QMyUser.myUser;
		return queryFactory
				.select(follow.count())
				.from(follow)
				.join(user).on(follow.followPk.followee.userId.eq(user.userId))
				.leftJoin(blockUser).on(blockUser.blockUserPk.myUser.userId.eq(userId).and(blockUser.blockUserPk.blockUser.userId.eq(user.userId)))
				.where(follow.followPk.follower.userId.eq(userId).and(follow.favorite.isTrue().and( blockUser.blockUserPk.myUser.isNull())))
				.fetchOne();
	}





}
