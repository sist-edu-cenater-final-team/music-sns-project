package com.github.musicsnsproject.service.music.external;

import com.github.musicsnsproject.common.myenum.MusicProvider;
import com.github.musicsnsproject.web.dto.music.external.ExternalChartResponse;

import com.github.musicsnsproject.web.dto.music.external.ExternalDetailResponse;
import com.github.musicsnsproject.web.dto.response.CustomSuccessResponse;
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
public class MelonChartService extends MusicChartService{
    @Value("${music.chart.melon}")
    private String url;

    @Override
    protected String getChartUrl() { return url; }

    @Override
    protected String getArtistSelector() { return ".wrap_song_info .rank02 span"; }

    @Override
    protected String getTitleSelector() { return ".wrap_song_info .rank01 span a"; }

    @Override
    protected String getAlbumSelector() { return ".wrap_song_info .rank03 a"; }

    @Override
    protected String getAlbumArtSelector() { return ".image_typeAll img"; }

    @Override
    protected String getAlbumArtAttr() { return "src"; }

    @Override
    protected String getSongNumberSelector() { return "tr[data-song-no]"; }

    @Override
    protected String getSongNumberAttr() { return "data-song-no"; }

    @Override
    protected boolean hasRankStatus() { return false; }
    @Override
    public MusicProvider musicProvider() {
        return MusicProvider.MELON;
    }
}
