package com.github.musicsnsproject.service.music.external;

<<<<<<< HEAD
import com.github.musicsnsproject.web.dto.music.external.ExternalChartResponse;
import com.github.musicsnsproject.web.dto.music.external.ExternalDetailResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VibeChartService {
    // Get Top100 Chart
    public List<ExternalChartResponse> getVibeChartTop100(String artistName) throws Exception {
        String url = "https://apis.naver.com/vibeWeb/musicapiweb/vibe/v1/chart/track/total";
        Document doc = Jsoup.connect(url).userAgent("Chrome").get();
        List<ExternalChartResponse> data = new ArrayList<>();
        for (Element element : doc.select("response > result > chart > items > tracks > track")) {
            String[] rank = getRankStatus(element).split(",");
            String searchedArtistName = element.select("album > artists > artist > artistName").text();
            if (artistName == null || searchedArtistName.contains(artistName)) {
                data.add(ExternalChartResponse.builder()
                        .rank(Integer.parseInt(element.select("rank > currentRank").text()))
                        .artistName(element.select("album > artists > artist > artistName").text())
                        .title(element.select("trackTitle").text())
                        .albumName(element.select("album > albumTitle").text())
                        .albumArt(element.select("album > imageUrl").text())
                        .songNumber(element.select("trackId").text())
                        .rankStatus(rank[0])
                        .changedRank(Integer.parseInt(rank[1]))
                        .build());
            }
        }
        return data;
    }

    // Get RankStatus
    private String getRankStatus(Element element) {
        String rankVariation = element.select("rank > rankVariation").text();
        if (rankVariation.equals("0")) {
            return "static,0";
        } else if (rankVariation.contains("-")) {
            return "down," + Math.abs(Integer.parseInt(rankVariation));
        } else {
            return "up," + rankVariation;
        }
    }

    // Find AlbumNames By ArtistName
    public List<ExternalDetailResponse> getAlbums(String artistName) throws Exception {
        String url = "https://apis.naver.com/vibeWeb/musicapiweb/v3/search/album?query="
                + artistName
                + "&start=1&display=100&sort=RELEVANCE";
        Document doc = Jsoup.connect(url).userAgent("Chrome").get();
        List<ExternalDetailResponse> data = new ArrayList<>();
        for (Element element : doc.select("response > result > albums > album")) {
            data.add(ExternalDetailResponse.builder()
                    .title(element.select("albumTitle").text())
                    .number(element.select("albumId").text())
                    .build());
        }
        return data;
    }

    // Find Songs By AlbumNumber
    public List<ExternalDetailResponse> getSongLists(String albumNumber) throws Exception {
        String url = "https://apis.naver.com/vibeWeb/musicapiweb/album/" + albumNumber + "/tracks";
        Document doc = Jsoup.connect(url).userAgent("Chrome").get();
        List<ExternalDetailResponse> data = new ArrayList<>();
        for (Element element : doc.select("response > result > tracks > track")) {
            data.add(ExternalDetailResponse.builder()
                    .title(element.select("trackTitle").text())
                    .number(element.select("trackId").text())
                    .build());
        }
        return data;
    }
}
=======
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
        return cssQueryTrack + " > album > artists > artist > artistName";
    }

    @Override
    protected String getTitleSelector() {
        return cssQueryTrack + " > trackTitle";
    }

    @Override
    protected String getAlbumSelector() {
        return cssQueryTrack + " > album > albumTitle";
    }

    @Override
    protected String getAlbumArtSelector() {
        return cssQueryTrack + " > album > imageUrl";
    }

    @Override
    protected String getAlbumArtAttr() {
        return "text"; // MusicChartService에서 text를 직접 가져오도록 커스터마이징 필요 시 수정
    }

    @Override
    protected String getSongNumberSelector() {
        return cssQueryTrack + " > trackId";
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

>>>>>>> branch 'main' of https://github.com/sist-edu-cenater-final-team/music-sns-project.git
