package com.github.musicsnsproject.web.controller.rest.profile;

import com.github.musicsnsproject.common.myenum.EmotionEnum;
import com.github.musicsnsproject.service.music.profile.ProfileMusicService;
import com.github.musicsnsproject.web.dto.profile.ProfileMusicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profileMusic")
public class ProfileMusicRestController {

    private final ProfileMusicService profileService;

    @GetMapping("/list")
    public ResponseEntity<List<ProfileMusicResponse>> getProfileMusicList(@AuthenticationPrincipal Long userId){

        List<ProfileMusicResponse> profileMusicList = profileService.getProfileMusicList(userId);

        return ResponseEntity.ok(profileMusicList);
    }

    // 프로필 음악리스트 추가하기
    @PostMapping("/add")
    public ResponseEntity<String> addProfileMusic(@AuthenticationPrincipal Long userId,
                                                  @RequestParam(value = "emotion") EmotionEnum emotion,
                                                  @RequestParam(value = "musicId") String musicId){

        profileService.addProfileMusic(userId, musicId, emotion);

        return ResponseEntity.ok("프로필 음악 추가되었습니다.");
    }

    // 프로필 음악리스트 삭제하기
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProfileMusic(@AuthenticationPrincipal Long userId,
                                                     @RequestParam(value="musicId") String musicId ) {

        profileService.deleteProfileMusic(userId, musicId);
        return ResponseEntity.ok("프로필 음악을 삭제하였습니다.");
    }
}
