package com.github.musicsnsproject.service.community.comment;

import com.github.musicsnsproject.common.exceptions.CustomNotFoundException;
import com.github.musicsnsproject.common.exceptions.DuplicateKeyException;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;
import com.github.musicsnsproject.repository.jpa.community.comment.Comment;
import com.github.musicsnsproject.repository.jpa.community.comment.CommentRepository;
import com.github.musicsnsproject.repository.jpa.community.post.Post;
import com.github.musicsnsproject.web.dto.comment.RequestCommentDTO;
import com.github.musicsnsproject.web.dto.comment.ResponseCommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MyUserRepository myUserRepository;

    @Transactional(readOnly = true)
    public List<ResponseCommentDTO> getCommentList(Long postId) {

        List<ResponseCommentDTO> reponseList = commentRepository.findByCommentIdAndMyUser(postId);

        if(reponseList.isEmpty()){
            reponseList = null;
        }
        else{
            reponseList.forEach(c -> {
                c.getReplyCount();
            });
        }



        return reponseList;
    }

    @Transactional
    public ResponseCommentDTO saveComments(Long userId, RequestCommentDTO requestCommentDTO) {
        Post post = Post.onlyId(requestCommentDTO.getPostId());
        Optional<MyUser> myUserOp = myUserRepository.findById(userId);
        MyUser myUser1 = myUserOp.orElseGet(()-> MyUser.onlyId(userId));
        MyUser myUser = myUserOp.orElseThrow(()->
                CustomNotFoundException.of().customMessage("해당 유저아이디의 유저가 존재하지 않습니다.").build());

        Comment parent = requestCommentDTO.getParentCommentId() != null ?
                         Comment.onlyId(requestCommentDTO.getParentCommentId()) : null;
        Comment saveComment = Comment.of(post, myUser, requestCommentDTO.getComment(), parent);

        try{
            commentRepository.save(saveComment);

            if(parent != null){
                Comment root = (parent.getRootComment() != null) ? parent.getRootComment() : parent;

            }

            return ResponseCommentDTO.builder()
                    .commentId(saveComment.getCommentId())
                    .parentCommentId(saveComment.getCommentId() == null ? null : saveComment.getParentComment().getCommentId())
                    .createdAt(saveComment.getCreatedAt())
                    .writerProfileImageUrl(saveComment.getMyUser().getProfileImage())
                    .writer(saveComment.getMyUser().getUsername())
                    .contents(saveComment.getContents())
                    .postId(saveComment.getPost().getPostId())
                    .build();

        }catch(Exception e){
            throw DuplicateKeyException.of()
                    .customMessage("충돌났음")
                    .systemMessage(e.getMessage())
                    .request(requestCommentDTO)
                    .build();
        }
    }
}
