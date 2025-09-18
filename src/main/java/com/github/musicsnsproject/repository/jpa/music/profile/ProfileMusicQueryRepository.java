package com.github.musicsnsproject.repository.jpa.music.profile;

import java.util.List;

import com.github.musicsnsproject.domain.ProfileMusicVO;

public interface ProfileMusicQueryRepository {

	List<ProfileMusicVO> emotionPlayList(Long emotionId);
	
	List<ProfileMusicVO> profileMusicId(Long userId);

    // myMusicId 중복체크
    boolean duplicateCheck(Long userId, String musicId);

    List<Long> findMyMusicIdsByUserId(Long userId);

    List<ProfileMusic> findMyMusics(Long userId);

    // 삭제할 프로필 음악 찾기
    ProfileMusic findDeleteByMusicId(Long userId, String musicId);

    List<ProfileMusic> findAllAfterDelete(Long userId, int deletedOrder);
}
