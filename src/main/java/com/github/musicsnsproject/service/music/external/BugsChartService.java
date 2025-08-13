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
import java.util.stream.Collectors;

@Service
public class BugsChartService extends MusicChartService{
    @Value("${music.chart.bugs}")
    private String url;

    @Override
    protected String getChartUrl() { return url; }

    @Override
    protected String getArtistSelector() { return "p.artist"; }

    @Override
    protected String getTitleSelector() { return "p.title"; }

    @Override
    protected String getAlbumSelector() { return ".left .album"; }

    @Override
    protected String getAlbumArtSelector() { return ".thumbnail img"; }

    @Override
    protected String getAlbumArtAttr() { return "src"; }

    @Override
    protected String getSongNumberSelector() { return ".trackList tbody > tr"; }

    @Override
    protected String getSongNumberAttr() { return "trackid"; }

    @Override
    protected boolean hasRankStatus() { return true; }

    @Override
    public MusicProvider musicProvider() {
        return MusicProvider.BUGS;
    }

    @Override
    protected List<String> getRankStatus(Document doc) {
        List<String> hasChangedList = new ArrayList<>();
        for (Element element : doc.select(".byChart tbody .ranking p")) {
            String className = element.className().split(" ")[1];
            String text = element.select("em").text();
            switch (className) {
                case "none" -> hasChangedList.add("static,0");
                case "up" -> hasChangedList.add("up," + text);
                case "down" -> hasChangedList.add("down," + text);
                case "new", "renew" -> hasChangedList.add("new,0");
            }
        }
        return hasChangedList;
    }
}
