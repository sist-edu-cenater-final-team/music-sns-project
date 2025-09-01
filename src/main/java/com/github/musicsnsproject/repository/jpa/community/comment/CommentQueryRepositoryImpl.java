package com.github.musicsnsproject.repository.jpa.community.comment;

import com.github.musicsnsproject.repository.jpa.account.user.QMyUser;
import com.github.musicsnsproject.repository.jpa.community.post.QPost;
import com.github.musicsnsproject.web.dto.comment.ResponseCommentDTO;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {
    private final JPAQueryFactory queryFactory;

    // 댓글 다 가져오기
    @Override
    public List<ResponseCommentDTO> findByCommentIdAndMyUser(Long postId) {

        QComment child = new QComment("child");

        List<ResponseCommentDTO> commentList = queryFactory
                .select(Projections.constructor(
                        ResponseCommentDTO.class,
                        QComment.comment.commentId,
                        QComment.comment.post.postId,
                        QComment.comment.contents,
                        QMyUser.myUser.username,
                        QMyUser.myUser.profileImage,
                        QComment.comment.createdAt,
                        QComment.comment.parentComment.commentId,
                        JPAExpressions
                                .select(Wildcard.count)
                                .from(child)
                                .where(child.rootComment.eq(QComment.comment)
                                        .and(child.ne(QComment.comment))) // 자기 자신 제외 (원댓글 자신은 빼고 하위만 카운트)
                ))
                .from(QComment.comment)
                .join(QComment.comment.post, QPost.post)
                .join(QComment.comment.myUser, QMyUser.myUser)
                .where(QComment.comment.post.postId.eq(postId))
                .orderBy(QComment.comment.createdAt.asc(),
                        QComment.comment.parentComment.commentId.asc())
                .fetch();


        return commentList;
    }
}
