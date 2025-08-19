package com.github.musicsnsproject.repository.jpa.community.post;

import com.github.musicsnsproject.repository.jpa.account.user.QMyUser;
import com.github.musicsnsproject.repository.jpa.emotion.QEmotion;
import com.github.musicsnsproject.repository.jpa.emotion.QUserEmotion;
import com.github.musicsnsproject.web.dto.temp.TempTestDto;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {
    private final JPAQueryFactory queryFactory;

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
