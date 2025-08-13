<<<<<<< HEAD
package com.github.musicsnsproject.web.controller.rest.music.external;

import com.github.musicsnsproject.service.music.external.MelonChartService;
import com.github.musicsnsproject.web.dto.music.external.ExternalChartResponse;
import com.github.musicsnsproject.web.dto.music.external.ExternalDetailResponse;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/melon")
@RequiredArgsConstructor
public class MelonChartController {
    private final MelonChartService melonChartService;



    @GetMapping({"/chart", "/chart/{artistName}"})
    public CustomSuccessResponse<List<ExternalChartResponse>> getMelonChartTop100ByArtistName(@PathVariable(required = false) String artistName) throws Exception {
        return CustomSuccessResponse.ofOk("멜론 음원 차트 조회 성공",melonChartService.getMelonChartTop100(artistName));
    }

    @GetMapping("/albums/{artistName}")
    public CustomSuccessResponse<List<ExternalDetailResponse>> getAlbums(@PathVariable String artistName) throws Exception {
        return CustomSuccessResponse.ofOk("멜론 아티스트 앨범 조회 성공", melonChartService.getAlbums(artistName));
    }

    @GetMapping("/songs/{albumNumber}")
    public CustomSuccessResponse<List<ExternalDetailResponse>> getSongs(@PathVariable String albumNumber) throws Exception {
        return CustomSuccessResponse.ofOk("멜론 앨범 내 곡 조회 성공", melonChartService.getSongLists(albumNumber));
    }

}
=======
//package com.github.musicsnsproject.web.controller.rest.music.external;
//
//import com.github.musicsnsproject.service.music.external.MelonChartService;
//import com.github.musicsnsproject.web.dto.music.external.ExternalChartResponse;
//import com.github.musicsnsproject.web.dto.music.external.ExternalDetailResponse;
//import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/music/melon")
//@RequiredArgsConstructor
//public class MelonChartController {
//    private final MelonChartService melonChartService;
//
//
//
//    @GetMapping({"/chart", "/chart/{artistName}"})
//    public CustomSuccessResponse<List<ExternalChartResponse>> getMelonChartTop100ByArtistName(@PathVariable(required = false) String artistName) throws Exception {
//        return CustomSuccessResponse.ofOk("멜론 음원 차트 조회 성공",melonChartService.getMelonChartTop100(artistName));
//    }
//
//    @GetMapping("/albums/{artistName}")
//    public CustomSuccessResponse<List<ExternalDetailResponse>> getAlbums(@PathVariable String artistName) throws Exception {
//        return CustomSuccessResponse.ofOk("멜론 아티스트 앨범 조회 성공", melonChartService.getAlbums(artistName));
//    }
//
//    @GetMapping("/songs/{albumNumber}")
//    public CustomSuccessResponse<List<ExternalDetailResponse>> getSongs(@PathVariable String albumNumber) throws Exception {
//        return CustomSuccessResponse.ofOk("멜론 앨범 내 곡 조회 성공", melonChartService.getSongLists(albumNumber));
//    }
//
//}
>>>>>>> branch 'main' of https://github.com/sist-edu-cenater-final-team/music-sns-project.git
