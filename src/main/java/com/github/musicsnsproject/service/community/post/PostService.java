package com.github.musicsnsproject.service.community.post;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.community.post.Post;
import com.github.musicsnsproject.repository.jpa.community.post.PostImage;
import com.github.musicsnsproject.repository.jpa.community.post.PostImageRepository;
import com.github.musicsnsproject.repository.jpa.community.post.PostRepository;
import com.github.musicsnsproject.repository.jpa.emotion.Emotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotionRepository;
import com.github.musicsnsproject.web.dto.post.FollowPostVO;
import com.github.musicsnsproject.web.dto.post.WriteRequest;
import com.github.musicsnsproject.web.dto.storage.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserEmotionRepository userEmotionRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    

    @Transactional
    public Long writePostByRequest(WriteRequest request, Long userId) {
        MyUser user = MyUser.onlyId(userId);
        System.out.println(request.getUserEmotion());
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

    //
    public List<FollowPostVO> followPostSelect(Long userId) {

        List<FollowPostVO> followPostVOList = postRepository.findFollowPostByUserId(userId);

        return followPostVOList;
    }

    public Long findbyCntByPostId(List<Long> postIdForLikeCnt) {

        Long n = postRepository.findbyCntByPostId(postIdForLikeCnt);

        return n;
    }
}
