package com.github.musicsnsproject.web.controller.rest.music.external;

import com.github.musicsnsproject.service.music.external.GenieChartService;

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
@RequestMapping("/api/genie")
@RequiredArgsConstructor
public class GenieChartController {

    private final GenieChartService genieChartService;

    @GetMapping({"/chart", "/chart/{artistName}"})
    public CustomSuccessResponse<List<ExternalChartResponse>> getGenieChartTop100ByArtistName(@PathVariable(required = false) String artistName) throws Exception {
        return CustomSuccessResponse.ofOk("Genie 음원 차트 조회 성공", genieChartService.getGenieChartTop100(artistName));
    }

    @GetMapping("/albums/{artistName}")
    public CustomSuccessResponse<List<ExternalDetailResponse>> getAlbums(@PathVariable String artistName) throws Exception {
        return CustomSuccessResponse.ofOk("Genie 아티스트 앨범 조회 성공", genieChartService.getAlbums(artistName));
    }

    @GetMapping("/songs/{albumNumber}")
    public CustomSuccessResponse<List<ExternalDetailResponse>> getSongs(@PathVariable String albumNumber) throws Exception {
        return CustomSuccessResponse.ofOk("Genie 앨범 내 곡 조회 성공", genieChartService.getSongLists(albumNumber));
    }




}
