package com.github.musicsnsproject.service.community.post;

import com.github.musicsnsproject.common.exceptions.CustomAccessDenied;
import com.github.musicsnsproject.common.exceptions.CustomNotFoundException;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.community.comment.CommentRepository;
import com.github.musicsnsproject.repository.jpa.community.like.LikeRepository;
import com.github.musicsnsproject.repository.jpa.community.post.Post;
import com.github.musicsnsproject.repository.jpa.community.post.PostImage;
import com.github.musicsnsproject.repository.jpa.community.post.PostImageRepository;
import com.github.musicsnsproject.repository.jpa.community.post.PostRepository;
import com.github.musicsnsproject.repository.jpa.emotion.Emotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotionRepository;
import com.github.musicsnsproject.web.dto.post.FollowPostVO;
import com.github.musicsnsproject.web.dto.post.MyPost;
import com.github.musicsnsproject.web.dto.post.PostEditRequest;
import com.github.musicsnsproject.web.dto.post.WriteRequest;
import com.github.musicsnsproject.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserEmotionRepository userEmotionRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;


    @Transactional
    public Long writePostByRequest(WriteRequest request, Long userId) {
        MyUser user = MyUser.onlyId(userId);
        // System.out.println(request.getUserEmotion());
        Emotion emotion = Emotion.fromEmotionValue(request.getUserEmotion());
        UserEmotion userEmotion = UserEmotion.fromUserEmotion(emotion, user);

        UserEmotion savedUserEmotion = userEmotionRepository.save(userEmotion); // userEmotionId 를 만들어주는 메소드


        Post newPost = Post.of(request, savedUserEmotion);

        postRepository.save(newPost);

        if(request.getImages() != null&&!request.getImages().isEmpty()){
            for(FileDto image : request.getImages()){
                String imageUrl = image.getFileUrl();
                String imageName = image.getFileName();
                PostImage imageEntity = PostImage.of(newPost, imageUrl, imageName);
                postImageRepository.save(imageEntity);
            }
        }

        return newPost.getPostId();

    }

    @Transactional(readOnly = true)
    public List<FollowPostVO> followPostSelect(Long userId) {

        List<FollowPostVO> followPostVOList = postRepository.findFollowPostByUserId(userId);

        return followPostVOList;
    }

    public Long findbyCntByPostId(List<Long> postIdForLikeCnt) {

        Long n = postRepository.findbyCntByPostId(postIdForLikeCnt);

        return n;
    }

    @Transactional
    public Long deletePost(Long userId, Long postId) {


        Post post = postRepository.findById(postId)
                .orElseThrow(() -> CustomNotFoundException.of().customMessage("해당 아이디의 게시글이 없습니다.").request(postId).build());

        if(!post.getUserEmotion().getMyUser().getUserId().equals(userId)){
            throw CustomAccessDenied.of().customMessage("본인 게시글만 삭제가능합니다.").request(userId).build();
        }
        else{
            // like 의 좋아요 지우기
            likeRepository.deleteByPostId(postId);
            // comment 의 댓글부터 지우기
            commentRepository.deleteByPostId(postId);
            // images 의 이미지 지우기
            postImageRepository.deleteByPostId(postId);
            // post 의 게시글 지우기
            postRepository.deleteById(postId);

            return postId;
        }


    }

    @Transactional(readOnly = true)
    public MyPost chosenMeSPosts(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> CustomNotFoundException.of().customMessage("해당 아이디의 게시글이 없습니다.").request(postId).build());

        if(!post.getUserEmotion().getMyUser().getUserId().equals(userId)){
            throw CustomAccessDenied.of().customMessage("본인 게시글만 수정가능합니다.").request(userId).build();
        }
        else{

            List<String> postImageUrlList = postImageRepository.findPostImageUrlsByPostId(postId);


            return MyPost.builder()
                    .title(post.getTitle())
                    .contents(post.getContents())
                    .postId(postId)
                    .userEmotion(post.getUserEmotion().getEmotion().getEmotionValue())
                    .imageUrls(postImageUrlList)
                    .build();
        }

    }

    @Transactional
    public void postEdit(PostEditRequest request,
                         Long userId) {

        MyUser user = MyUser.onlyId(userId);
        Emotion emotion = Emotion.fromEmotionValue(request.getUserEmotion());
        UserEmotion transientUserEmotion = UserEmotion.fromUserEmotion(emotion, user);
        // useremotionId 만들어주기
        UserEmotion userEmotion = userEmotionRepository.save(transientUserEmotion);

        int updated = postRepository.updatePostForEdit(
                request.getPostId(),
                request.getTitle(),
                request.getContents(),
                userEmotion
        );
        if (updated == 0) {
            throw new IllegalArgumentException("게시물이 없거나 수정 실패했습니다.");
        }


    }
}
