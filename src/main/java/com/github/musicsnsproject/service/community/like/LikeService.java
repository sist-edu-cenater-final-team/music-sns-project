package com.github.musicsnsproject.service.community.like;

import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.repository.jpa.community.like.Like;
import com.github.musicsnsproject.repository.jpa.community.like.LikePk;
import com.github.musicsnsproject.repository.jpa.community.like.LikeRepository;
import com.github.musicsnsproject.repository.jpa.community.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    
    @Transactional
    public void insertLikeByPostIdUserId(Long postId, Long testUserId) {

        LikePk likePk = LikePk.onlyId(testUserId, postId);
        Like like = Like.onlyId(likePk);
        likeRepository.save(like);
    }
    
    @Transactional
    public boolean isLiked(Long postId, Long testUserId) {
        LikePk likePk = LikePk.onlyId(testUserId, postId);
        
        if(likeRepository.existsById(likePk)) { // 좋아요 취소하기
            likeRepository.deleteById(likePk);
            return false;
        }
        else { // 좋아요 누르기
            Like like = Like.onlyId(likePk);
            likeRepository.save(like);
            return true;
        }
        
    }

}
