package com.github.musicsnsproject.service.music.external;

import com.github.musicsnsproject.common.exceptions.CustomBindException;
import com.github.musicsnsproject.common.myenum.MusicProvider;
import com.github.musicsnsproject.web.dto.music.external.ExternalChartResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class MusicChartService {
    public abstract MusicProvider musicProvider();
    protected Document getDoc(String url){
        try{
            return Jsoup.connect(url).userAgent("Chrome").get();
        }catch (IOException e){
            throw CustomBindException.of().systemMessage(e.getMessage()).customMessage("Jsoup 문서 로딩실패").build();
        }
    }

    public List<ExternalChartResponse> getTop100(String artistName){
        Document doc = getDoc(getChartUrl());

        List<String> artistNames = getTextsOfElements(doc, getArtistSelector());
        List<String> titles = getTextsOfElements(doc, getTitleSelector());
        List<String> albumNames = getTextsOfElements(doc, getAlbumSelector());
        List<String> albumArts = getAttrsOfElements(doc, getAlbumArtSelector(), getAlbumArtAttr());
        List<String> songNumbers = getAttrsOfElements(doc, getSongNumberSelector(), getSongNumberAttr());

        List<String> rankStatuses = null;
        if (hasRankStatus()) {
            rankStatuses = getRankStatus(doc);
        } else {
            // rank 정보 없는 경우 static 처리
            rankStatuses = titles.stream().map(t -> "static,0").toList();
        }
        if(this instanceof BugsChartService)
            rankStatuses.add("up,1");
        List<ExternalChartResponse> data = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            if (artistName == null || artistNames.get(i).contains(artistName)) {


                String[] rank = rankStatuses.get(i).split(",");
                data.add(ExternalChartResponse.builder()
                        .rank(i + 1)
                        .artistName(artistNames.get(i))
                        .title(titles.get(i))
                        .albumName(albumNames.get(i))
                        .albumArt(albumArts.get(i))
                        .songNumber(songNumbers.get(i))
                        .rankStatus(rank[0])
                        .changedRank(Integer.parseInt(rank[1]))
                        .build());
            }
        }
        return data;
    }

    protected List<String> getTextsOfElements(Document doc, String selector) {
        return doc.select(selector).stream()
                .map(Element::text)
                .toList();
    }

    protected List<String> getAttrsOfElements(Document doc, String selector, String attr) {
        return doc.select(selector).stream()
                .map(element -> element.attr(attr).isEmpty() ? element.text() : element.attr(attr))
                .toList();
    }

    // RankStatus 추출 (기본 구현은 override)
    protected List<String> getRankStatus(Document doc) {
        return List.of();
    }

    // 서브클래스에서 구현할 부분
    protected abstract String getChartUrl();
    protected abstract String getArtistSelector();
    protected abstract String getTitleSelector();
    protected abstract String getAlbumSelector();
    protected abstract String getAlbumArtSelector();
    protected abstract String getAlbumArtAttr();
    protected abstract String getSongNumberSelector();
    protected abstract String getSongNumberAttr();
    protected abstract boolean hasRankStatus();
}



