package com.github.musicsnsproject.web.controller.rest.music.external;

import com.github.musicsnsproject.common.exceptions.CustomNotFoundException;
import com.github.musicsnsproject.common.myenum.MusicProvider;
import com.github.musicsnsproject.config.client.oauth.OAuthApiClient;
import com.github.musicsnsproject.service.music.external.MusicChartService;
import com.github.musicsnsproject.web.dto.music.external.ExternalChartResponse;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/music/{provider}")
public class MusicChartController {
    private final Map<MusicProvider, MusicChartService> musicChartServiceMap;
    public MusicChartController(List<MusicChartService> services) {
        this.musicChartServiceMap = services.stream().collect(
                Collectors.toUnmodifiableMap(MusicChartService::musicProvider, Function.identity())
        );
    }
    @GetMapping({"/chart", "/chart/{artistName}"})
    public CustomSuccessResponse<List<ExternalChartResponse>> getChartTop100ByArtistName(@AuthenticationPrincipal Long userId,
            @PathVariable MusicProvider provider,@PathVariable(required = false) String artistName){
        MusicChartService musicChartService = musicChartServiceMap.get(provider);
        List<ExternalChartResponse> response = musicChartService.getTop100(artistName);
        return CustomSuccessResponse.ofOk(provider.getValue()+" 차트 조회 성공",response);
    }


}
