package com.github.musicsnsproject.service.musicplaylist;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.musicsnsproject.domain.ProfileMusicVO;
import com.github.musicsnsproject.repository.jpa.account.user.MyUserRepository;
import com.github.musicsnsproject.repository.jpa.music.profile.ProfileMusicQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicPlayListService_imple implements MusicPlayListService {

	private final ProfileMusicQueryRepository repository;

	@Override
	public List<ProfileMusicVO> emotionPlayList(Long emotionId) {
		return repository.emotionPlayList(emotionId);
	}
}
