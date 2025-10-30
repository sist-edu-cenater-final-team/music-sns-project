package com.github.musicsnsproject.service.music.external;

import com.github.musicsnsproject.common.myenum.MusicProvider;
import com.github.musicsnsproject.web.dto.music.external.ExternalChartResponse;
import com.github.musicsnsproject.web.dto.music.external.ExternalDetailResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class VibeChartService extends MusicChartService {

    @Value("${music.chart.vibe}")
    private String url;

    private final String cssQueryTrack = "response > result > chart > items > tracks > track";

    @Override
    protected String getChartUrl() {
        return url;
    }

    @Override
    protected String getArtistSelector() {
        return " > album > artists > artist > artistName";
    }

    @Override
    protected String getTitleSelector() {
        return " > trackTitle";
    }

    @Override
    protected String getAlbumSelector() {
        return " > album > albumTitle";
    }

    @Override
    protected String getAlbumArtSelector() {
        return this.cssQueryTrack + " > album > imageUrl";
    }

    @Override
    protected String getAlbumArtAttr() {
        return "text"; // MusicChartService에서 text를 직접 가져오도록 커스터마이징 필요 시 수정
    }

    @Override
    protected String getSongNumberSelector() {
        return this.cssQueryTrack + " > trackId";
    }

    @Override
    protected String getSongNumberAttr() {
        return "text";
    }

    @Override
    protected boolean hasRankStatus() {
        return true;
    }

    @Override
    protected List<String> getRankStatus(Document doc) {
        return doc.select(cssQueryTrack)
                .stream()
                .map(this::getRankStatusFromElement)
                .toList();
    }
    @Override
    protected List<String> getTextsOfElements(Document doc, String cssQuery){
        List<String> results = new ArrayList<>();
        for (Element element : doc.select(cssQueryTrack)){
            String text = element.select(cssQuery).text();
            results.add(text);
        }


        return results;
    }

    private String getRankStatusFromElement(Element element) {
        String rankVariation = element.select("rank > rankVariation").text();
        if (rankVariation.equals("0")) {
            return "static,0";
        } else if (rankVariation.contains("-")) {
            return "down," + Math.abs(Integer.parseInt(rankVariation));
        } else {
            return "up," + rankVariation;
        }
    }

    // 추가 기능 - 앨범 번호로 곡 조회
    public List<ExternalDetailResponse> getSongLists(String albumNumber) throws Exception {
        String trackUrl = "https://apis.naver.com/vibeWeb/musicapiweb/album/" + albumNumber + "/tracks";
        Document doc = Jsoup.connect(trackUrl).userAgent("Chrome").get();
        return doc.select("response > result > tracks > track").stream()
                .map(e -> ExternalDetailResponse.builder()
                        .title(e.select("trackTitle").text())
                        .number(e.select("trackId").text())
                        .build())
                .toList();
    }
    @Override
    public MusicProvider musicProvider() {
        return MusicProvider.VIBE;
    }
}

