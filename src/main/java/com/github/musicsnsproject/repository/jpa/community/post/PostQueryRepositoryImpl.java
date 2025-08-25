package com.github.musicsnsproject.repository.jpa.community.post;

import com.github.musicsnsproject.repository.jpa.account.follow.QFollow;
import com.github.musicsnsproject.repository.jpa.community.like.QLike;
import com.github.musicsnsproject.web.dto.post.FollowPostVO;
import com.github.musicsnsproject.repository.jpa.account.user.QMyUser;
import com.github.musicsnsproject.repository.jpa.emotion.QEmotion;
import com.github.musicsnsproject.repository.jpa.emotion.QUserEmotion;
import com.github.musicsnsproject.web.dto.temp.TempTestDto;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<FollowPostVO> findFollowPostByUserId(Long userId) {
        QPost post = QPost.post;
        QUserEmotion ue = QUserEmotion.userEmotion;
        QMyUser author = QMyUser.myUser;
        QPostImage postImage = QPostImage.postImage;
        QFollow follow = QFollow.follow;

        return queryFactory
                .from(post)
                .join(post.userEmotion, ue)
                .join(ue.myUser, author)
                .leftJoin(post.images, postImage)
                .leftJoin(QLike.like).on(QLike.like.likePk.post.eq(post))
                .where(
                        author.userId.eq(userId)
                                .or(JPAExpressions
                                        .selectOne()
                                        .from(follow)
                                        .where(
                                                follow.followPk.follower.userId.eq(userId)
                                                        .and(follow.followPk.followee.eq(author))
                                        )
                                        .exists()
                                )
                )
                .orderBy(post.createdAt.desc())
                .transform(
                        GroupBy.groupBy(post.postId)
                                .list(
                                        Projections.fields(FollowPostVO.class,
                                                // List로 수집하여 VO 타입과 일치시킴
                                                GroupBy.list(postImage.postImageUrl).as("post_image_urls"),
                                                author.username.as("username"),
                                                author.profileImage.as("profileImage"),
                                                post.title.as("title"),
                                                post.contents.as("contents"),
                                                post.viewCount.as("viewCount"),
                                                post.postId.as("postId"),
                                                author.userId.as("userId"),
                                                GroupBy.list(QLike.like.likePk.myUser.userId).as("likedUserPks")
                                        )
                                )
                );
    }

    // 해당 게시물의 좋아요 총 개수를 얻기 위한 것
    @Override
    public Long findbyCntByPostId(List<Long> postIdForLikeCnt) {

        Long n = queryFactory.select(Wildcard.count)
                .from(QLike.like)
                .where(QLike.like.likePk.post.postId.in(postIdForLikeCnt))
                .fetchOne();
        return n;
    }


    public void test () {
    	
    	
        List<Post> a = queryFactory.selectFrom(QPost.post)
                .join(QPost.post.userEmotion, QUserEmotion.userEmotion).fetchJoin()
                .join(QUserEmotion.userEmotion.myUser, QMyUser.myUser).fetchJoin()
                .join(QUserEmotion.userEmotion.emotion, QEmotion.emotion).fetchJoin()
                .join(QPost.post.images, QPostImage.postImage).fetchJoin()
                .fetch();
        List<TempTestDto> b = queryFactory.select(
                                QMyUser.myUser.username.as("name"),
                                QPost.post.contents,
                                QPostImage.postImage.postImageUrl
                )
                .from(QPost.post)
                .join(QPost.post.userEmotion, QUserEmotion.userEmotion)
                .join(QUserEmotion.userEmotion.myUser, QMyUser.myUser)
                .join(QUserEmotion.userEmotion.emotion, QEmotion.emotion)
                .join(QPost.post.images, QPostImage.postImage)
                .transform(
                        GroupBy.groupBy(QPost.post.postId)
                                .list(
                                        Projections.fields(TempTestDto.class,
                                                GroupBy.list(QPost.post.images).as("imageUrl"),
                                                QPost.post.contents,
                                                QMyUser.myUser.username.as("name")
                                        ).as("imageUrls")
                                )
                );
    }
}
