package com.github.musicsnsproject.web.controller.rest.music.external;

import com.github.musicsnsproject.service.music.external.VibeChartService;
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
@RequestMapping("/api/vibe")
@RequiredArgsConstructor
public class VibeChartController {
    private final VibeChartService vibeChartService;


    @GetMapping({"/chart", "/chart/{artistName}"})
    public CustomSuccessResponse<List<ExternalChartResponse>> getVibeChartTop100ByArtistName(@PathVariable(required = false) String artistName) throws Exception {
        return CustomSuccessResponse.ofOk("VIBE 음원 차트 조회 성공",vibeChartService.getVibeChartTop100(artistName));
    }

    @GetMapping("/albums/{artistName}")
    public CustomSuccessResponse<List<ExternalDetailResponse>> getAlbums(@PathVariable String artistName) throws Exception {
        return CustomSuccessResponse.ofOk("VIBE 아티스트 앨범 조회 성공", vibeChartService.getAlbums(artistName));
    }

    @GetMapping("/songs/{albumNumber}")
    public CustomSuccessResponse<List<ExternalDetailResponse>> getSongs(@PathVariable String albumNumber) throws Exception {
        return CustomSuccessResponse.ofOk("VIBE 앨범 내 곡 조회 성공", vibeChartService.getSongLists(albumNumber));
    }
}
