package com.github.musicsnsproject.repository.jpa.music.profile;

import java.util.List;

import com.github.musicsnsproject.domain.ProfileMusicVO;

public interface ProfileMusicQueryRepository {

	List<ProfileMusicVO> emotionPlayList(Long emotionId);
	
	List<ProfileMusicVO> profileMusicId(Long userId);

    // myMusicId 찾기
    Long findMyMusicId(Long userId, String musicId);

    // myMusicId 중복체크
    boolean duplicateCheck(Long userId, String musicId, Long myMusicId);


    List<String> getAddMusicId(Long userId, String musicId);
}
