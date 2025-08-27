package com.github.musicsnsproject.repository.jpa.community.comment;

import com.github.musicsnsproject.repository.jpa.account.user.QMyUser;
import com.github.musicsnsproject.repository.jpa.community.post.QPost;
import com.github.musicsnsproject.web.dto.comment.ResponseCommentDTO;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CommentQueryRepositoryImpl implements CommentQueryRepository {
    private final JPAQueryFactory queryFactory;

    // 댓글 다 가져오기
    @Override
    public List<ResponseCommentDTO> findByCommentIdAndMyUser(Long postId) {

        List<Tuple> tuple = queryFactory.select(
                    QMyUser.myUser.username.as("writer"),
                    QMyUser.myUser.profileImage.as("writerProfileImageUrl"),
                    QComment.comment.commentId.as("commentId"),
                    QComment.comment.contents.as("contents"),
                    QComment.comment.createdAt.as("createdAt"),
                    QPost.post.postId.as("postId"),
                    QComment.comment.parentComment.commentId.as("parentCommentId")
            )
                .from(QComment.comment)
                .join(QComment.comment.post, QPost.post)
                .join(QComment.comment.myUser, QMyUser.myUser)
                .where(QComment.comment.post.postId.eq(postId))
                .orderBy(QComment.comment.createdAt.desc())
                .fetch();


        List<ResponseCommentDTO> commentList = tuple.stream()
                .map(t-> ResponseCommentDTO.builder()
                        .writer(t.get(QMyUser.myUser.username))
                        .writerProfileImageUrl(t.get(QMyUser.myUser.profileImage))
                        .commentId(t.get(QComment.comment.commentId))
                        .contents(t.get(QComment.comment.contents))
                        .parentCommentId(t.get(QComment.comment.parentComment.commentId))
                        .createdAt(t.get(QComment.comment.createdAt))
                        .postId(t.get(QPost.post.postId))
                        .build()
                ).toList();


        return commentList;
    }
}
