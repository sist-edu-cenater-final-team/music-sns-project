package com.github.musicsnsproject.web.controller.rest.profile;

import com.github.musicsnsproject.service.music.profile.ProfileService;
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

    private final ProfileService profileService;

    @GetMapping("/list")
    public ResponseEntity<List<ProfileMusicResponse>> getProfileMusicList(@AuthenticationPrincipal Long userId,
                                                                          @RequestParam(value = "musicId") String musicId){

        List<ProfileMusicResponse> profileMusicList = profileService.getProfileMusicList(userId, musicId);

        return ResponseEntity.ok(profileMusicList);
    }

    // 프로필 음악리스트 추가하기
    @PostMapping("/add")
    public ResponseEntity<String> addProfileMusic(@AuthenticationPrincipal Long userId,
                                                  @RequestParam(value = "musicId") String musicId,
                                                  @RequestParam(value = "emotionId") Long emotionId){

        profileService.addProfileMusic(userId, musicId, emotionId);

        return ResponseEntity.ok("프로필 음악 추가되었습니다.");
    }
}
