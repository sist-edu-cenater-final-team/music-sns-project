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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Service
public class GenieChartService extends MusicChartService {

    @Value("${music.chart.genie.url1}")
    private String url1;

    @Value("${music.chart.genie.url2}")
    private String url2;

    @Override
    protected String getChartUrl() {
        // MusicChartService의 getTop100()이 단일 URL만 지원하면,
        // Genie는 페이지가 2개라 따로 오버라이드해서 합칠 수도 있음.
        // 단순히 첫 페이지 URL을 반환
        return url1;
    }

    @Override
    protected String getArtistSelector() {
        return "table .artist";
    }

    @Override
    protected String getTitleSelector() {
        return "table .title";
    }

    @Override
    protected String getAlbumSelector() {
        return "table .albumtitle";
    }

    @Override
    protected String getAlbumArtSelector() {
        return "table .cover img";
    }

    @Override
    protected String getAlbumArtAttr() {
        return "src";
    }

    @Override
    protected String getSongNumberSelector() {
        return "table tr[songid]";
    }

    @Override
    protected String getSongNumberAttr() {
        return "songid";
    }

    @Override
    protected boolean hasRankStatus() {
        return true;
    }

    @Override
    protected List<String> getRankStatus(Document doc) {
        List<String> hasChangedList = new ArrayList<>();
        for (Element element : doc.select("table span.rank>span.rank>span")) {
            String className = element.className();
            String text = element.ownText();
            switch (className) {
                case "rank-none":
                    hasChangedList.add("static,0");
                    break;
                case "rank-up":
                    hasChangedList.add("up," + text);
                    break;
                case "rank-down":
                    hasChangedList.add("down," + text);
                    break;
                case "rank-new":
                    hasChangedList.add("new,0");
                    break;
            }
        }
        return hasChangedList;
    }

    // Genie만의 Top100 병합 처리
    @Override
    public List<ExternalChartResponse> getTop100(String artistName)  {
        Document doc1 = getDoc(url1);
        Document doc2 = getDoc(url2);

        List<String> artistNames = new ArrayList<>(getTextsOfElements(doc1, getArtistSelector()));
        artistNames.addAll(getTextsOfElements(doc2, getArtistSelector()));

        List<String> titles = new ArrayList<>(getTextsOfElements(doc1, getTitleSelector()));
        titles.addAll(getTextsOfElements(doc2, getTitleSelector()));

        List<String> albumNames = new ArrayList<>(getTextsOfElements(doc1, getAlbumSelector()));
        albumNames.addAll(getTextsOfElements(doc2, getAlbumSelector()));

        List<String> albumArts = new ArrayList<>(getAttrsOfElements(doc1, getAlbumArtSelector(), getAlbumArtAttr()));
        albumArts.addAll(getAttrsOfElements(doc2, getAlbumArtSelector(), getAlbumArtAttr()));

        List<String> songNumbers = new ArrayList<>(getAttrsOfElements(doc1, getSongNumberSelector(), getSongNumberAttr()));
        songNumbers.addAll(getAttrsOfElements(doc2, getSongNumberSelector(), getSongNumberAttr()));

        List<String> rankStatuses = new ArrayList<>(getRankStatus(doc1));
        rankStatuses.addAll(getRankStatus(doc2));

        List<ExternalChartResponse> data = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            String[] rank = rankStatuses.get(i).split(",");
            if (artistName == null || artistNames.get(i).contains(artistName)) {
                data.add(ExternalChartResponse.builder()
                        .rank(i + 1)
                        .artistName(artistNames.get(i))
                        .title(titles.get(i))
                        .albumName(albumNames.get(i))
                        .albumArt("https://" + albumArts.get(i).split("//")[1])
                        .songNumber(songNumbers.get(i))
                        .rankStatus(rank[0])
                        .changedRank(Integer.parseInt(rank[1]))
                        .build());
            }
        }
        return data;
    }@Override
    public MusicProvider musicProvider() {
        return MusicProvider.GENIE;
    }
}
