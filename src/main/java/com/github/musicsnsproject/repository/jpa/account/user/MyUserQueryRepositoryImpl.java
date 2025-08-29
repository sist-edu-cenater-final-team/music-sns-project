package com.github.musicsnsproject.repository.jpa.account.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.github.musicsnsproject.common.exceptions.CustomBadRequestException;
import com.github.musicsnsproject.common.myenum.Gender;
import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.domain.PostVO;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.repository.jpa.account.follow.QFollow;
import com.github.musicsnsproject.repository.jpa.account.history.login.QLoginHistory;
import com.github.musicsnsproject.repository.jpa.account.role.QRole;
import com.github.musicsnsproject.repository.jpa.account.socialid.QSocialId;
import com.github.musicsnsproject.repository.jpa.account.socialid.SocialIdPk;
import com.github.musicsnsproject.repository.jpa.community.post.QPost;
import com.github.musicsnsproject.repository.jpa.community.post.QPostImage;
import com.github.musicsnsproject.repository.jpa.emotion.QUserEmotion;
import com.github.musicsnsproject.web.dto.chat.ChatUserInfo;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class MyUserQueryRepositoryImpl implements MyUserQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QMyUser qMyUser = QMyUser.myUser;

    @Override
    public Optional<MyUser> findBySocialIdPkOrUserEmail(SocialIdPk socialIdPk, String email) {
        BooleanExpression emailPredicate = email != null ? qMyUser.email.eq(email) : null;
        QSocialId qSocialId = QSocialId.socialId;
        List<MyUser> myUserList = queryFactory.select(qMyUser)
                .from(qMyUser)
                .leftJoin(qMyUser.socialIds, qSocialId).fetchJoin()
                .leftJoin(qMyUser.roles, QRole.role).fetchJoin()
                .where(qSocialId.socialIdPk.eq(socialIdPk).or(emailPredicate))
                .fetch();
        MyUser response = singleOutAUser(myUserList, socialIdPk);
        return Optional.ofNullable(response);
    }
//    @Override
//    public Optional<CustomUserDetails> findBySocialIdPkOrUserEmailForAuth(SocialIdPk socialIdPk, String email) {
//        QSocialId qSocialId = QSocialId.socialId;
//        List<CustomUserDetails> userDetails = queryFactory
//                .from(qMyUser)
//                .leftJoin(qMyUser.socialIds, qSocialId)
//                .where(qSocialId.socialIdPk.eq(socialIdPk).or(qMyUser.email.eq(email)))
//                .transform(Projections.fields(CustomUserDetails.class,
//                        )
//
//                )
//
//
//        return Optional.empty();
//    }

    private BooleanExpression emailOrPhoneNumberPredicate(String emailOrPhoneNumber) {
        if (emailOrPhoneNumber.matches("01\\d{9}")) {
            return qMyUser.phoneNumber.eq(emailOrPhoneNumber);
        } else if (emailOrPhoneNumber.matches(".+@.+\\..+")) {
            return qMyUser.email.eq(emailOrPhoneNumber);
        }
        throw CustomBadRequestException.of()
                .customMessage("잘못 입력된 식별자")
                .request(emailOrPhoneNumber)
                .build();
    }

    @Override
    public Optional<CustomUserDetails> findByEmailOrPhoneNumberForAuth(String emailOrPhoneNumber) {
        BooleanExpression emailOrPhoneNumberPredicate = emailOrPhoneNumberPredicate(emailOrPhoneNumber);

        List<CustomUserDetails> user = queryFactory
                .from(qMyUser)
                .join(qMyUser.roles, QRole.role)
                .leftJoin(qMyUser.loginHistories, QLoginHistory.loginHistory)
                .where(emailOrPhoneNumberPredicate)
                .transform(
                        GroupBy.groupBy(qMyUser.userId).list(
                                Projections.fields(CustomUserDetails.class,
                                        qMyUser.userId,
                                        qMyUser.email,
                                        qMyUser.nickname,
                                        qMyUser.password,
                                        qMyUser.failureCount,
                                        qMyUser.status,
                                        qMyUser.failureAt,
                                        qMyUser.registeredAt,
                                        qMyUser.withdrawalAt,
                                        GroupBy.set(QRole.role.name).as("roles"),
                                        ExpressionUtils.as(
                                                JPAExpressions
                                                        .select(QLoginHistory.loginHistory.loggedAt.max())
                                                        .from(QLoginHistory.loginHistory)
                                                        .where(QLoginHistory.loginHistory.myUser.eq(qMyUser)),
                                                "latestLoggedAt"
                                        )
                                ))

                );

        return Optional.ofNullable(user.size() == 1 ? user.get(0) : null);
    }

    @Override
    public void updateFailureCountByEmail(CustomUserDetails failUser) {
        queryFactory.update(qMyUser)
                .set(qMyUser.failureCount, failUser.getFailureCount())
                .set(qMyUser.failureAt, failUser.getFailureAt())
                .set(qMyUser.status, failUser.getStatus())
                .where(qMyUser.email.eq(failUser.getEmail()))
                .execute();
    }


    /**
     * 만약 소셜아이디로 가입이 되어있지만 해당 계정의 소셜이메일로 다른계정이 가입되어있을경우
     * 여러 계정중 소셜아이디의 계정으로 로그인 시도. 또는 소셜아이디는 찾을 수 없지만 소셜이메일과 같은 계정이 존재할 경우엔
     * 그냥 해당계정으로 로그인시도 (상위 메서드에서 소셜아이디를 추가 해줌)
     */
    private MyUser singleOutAUser(List<MyUser> myUserList, SocialIdPk socialIdPk) {
        return myUserList.isEmpty() ? null :
                myUserList.size() == 1 ? myUserList.get(0) :
                        myUserList.stream().filter(user ->
                                        user.getSocialIds().stream().anyMatch(id -> id.getSocialIdPk().equals(socialIdPk)))
                                .findFirst().orElse(null);
    }

    
    
	@Override
	public List<PostVO> getUserPost(Long userId) {
		QPost post = QPost.post;
		QUserEmotion emotion = QUserEmotion.userEmotion;
		QPostImage image = QPostImage.postImage;
		
		return 	queryFactory.select(
					emotion.myUser.userId,
					post.postId,
					image.postImageUrl,
					post.title
				)
				.from(post)
				.join(emotion)
				.on(post.userEmotion.userEmotionId.eq(emotion.userEmotionId))
				.leftJoin(image)
				.on(image.post.postId.eq(post.postId))
				.where(emotion.myUser.userId.eq(userId))
				.transform(
						GroupBy.groupBy(post.postId)
						.list(Projections.fields(PostVO.class, 
								emotion.myUser.userId,
								post.postId,
								post.title,
								GroupBy.list(image.postImageUrl).as("postImageUrl")
								)
						)
				);
	}
	@Override
	public MyUserVO getUserInfo(Long userId) {
		
		QMyUser user = QMyUser.myUser;
		QFollow follow = QFollow.follow;
		QFollow follow2 = new QFollow("follow2");
		QUserEmotion emotion = QUserEmotion.userEmotion;
		return queryFactory
			    .select(Projections.fields(MyUserVO.class,
			            user.userId,
			            user.nickname,
			            user.username,
			            user.gender,
			            user.email,
			            user.profileImage.as("profile_image"),
			            user.profileMessage,
			            user.coin,
			            ExpressionUtils.as(
			                JPAExpressions
			                    .select(follow.followPk.follower.userId.countDistinct())
			                    .from(follow)
			                    .where(follow.followPk.followee.userId.eq(user.userId)),
			                "followerCount"   
			            ),
			            ExpressionUtils.as(
			                JPAExpressions
			                    .select(follow2.followPk.followee.userId.countDistinct())
			                    .from(follow2)
			                    .where(follow2.followPk.follower.userId.eq(user.userId)),
			                "followeeCount"  
			            ),
			            ExpressionUtils.as(
			            		JPAExpressions
			            			.select(emotion.userEmotionId.countDistinct())
			            			.from(emotion)
			            			.where(emotion.myUser.userId.eq(user.userId))
			            			, "postCount")
			        ))
			        .from(user)
			        .where(user.userId.eq(userId))
			        .fetchOne();

	}

	@Override
	@Transactional
	public long updateUserInfo(Map<String, Object> paraMap) {
		QMyUser user = QMyUser.myUser;
		

	    if (paraMap.get("profile_image") != null) {

			return queryFactory.update(user)
					.set(user.profileImage, String.valueOf(paraMap.get("profile_image")))
					.set(user.profileMessage, String.valueOf(paraMap.get("profileMessage")))
					.set(user.nickname, String.valueOf(paraMap.get("nickname")))
					.set(user.gender, (Gender)paraMap.get("gender"))
					.where(user.userId.eq((Long) paraMap.get("userId")))
					.execute();
	    }
	    else {

			return queryFactory.update(user)
					.set(user.profileMessage, String.valueOf(paraMap.get("profileMessage")))
					.set(user.nickname, String.valueOf(paraMap.get("nickname")))
					.set(user.gender, (Gender)paraMap.get("gender"))
					.where(user.userId.eq((Long) paraMap.get("userId")))
					.execute();
	    }

		
		
	}

	@Override
	public boolean isFollow(Map<String, Long> map) {
		QFollow follow = QFollow.follow;

		return queryFactory.selectOne()
		        .from(follow)
		        .where(
		            follow.followPk.follower.userId.eq(map.get("userId"))
		            .and(follow.followPk.followee.userId.eq(map.get("targetId")))
		            )
		        .fetchFirst() != null;
	}


    @Override
    public List<ChatUserInfo> findAllByIdForChatRoom(Set<Long> allOtherIds) {
        List<ChatUserInfo> response = queryFactory.from(qMyUser)
                .where(qMyUser.userId.in(allOtherIds))
                .select(Projections.fields(ChatUserInfo.class,
                        qMyUser.userId,
                        qMyUser.nickname,
                        qMyUser.profileImage.as("profileImageUrl")
                ))
                .fetch();
        if (response.size() != allOtherIds.size()) {
            throw CustomBadRequestException.of()
                    .customMessage("채팅방에 존재하지 않는 유저가 있습니다.")
                    .request(allOtherIds)
                    .build();
        }
        return response;
    }
}
