package com.github.musicsnsproject.service.mypage.eumpyo;

import com.github.musicsnsproject.repository.mybatis.dao.eumpyo.EumpyoHistoryDAO;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.dto.music.spotify.artist.SimplifiedArtist;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponseV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;   
import java.util.regex.Pattern;
import java.util.stream.Collectors;   

@Service
@RequiredArgsConstructor
public class EumpyoHistoryService_imple implements EumpyoHistoryService {

    private final EumpyoHistoryDAO dao;
    private final SpotifyMusicService spotifyMusicService;

    // 충전내역
    @Override
    public Map<String, Object> getChargeHistory(long userId, int page, int size) {
    	
        size = Math.max(1, size); 
        
        int totalCount = dao.countChargeHistory(userId); // 총 건수
        int totalPage  = (int) Math.ceil((double) totalCount / size); // 총 페이지 수
        int current    = Math.max(1, Math.min(page, Math.max(totalPage, 1))); 
        int offset     = (current - 1) * size; 

        List<Map<String, Object>> list = dao.findChargeHistoryPage(userId, offset, size); 

        Map<String, Object> map = new HashMap<>(); 
        
        map.put("result", "success");
        map.put("list", list);
        map.put("totalCount", totalCount);
        map.put("totalPage", totalPage);
        map.put("page", current);
        map.put("size", size);
        map.put("pageBar", makePageBar(totalCount, size, current, "/api/mypage/eumpyo/history/charge"));
        
        return map;
    }

    
    // 구매내역
    @Override
    public Map<String, Object> getPurchaseHistory(long userId, int page, int size) {

        size = Math.max(1, size);

        int totalCount = dao.countPurchaseHistory(userId);
        int totalPage  = (int) Math.ceil((double) totalCount / size);
        int current    = Math.max(1, Math.min(page, Math.max(totalPage, 1)));
        int offset     = (current - 1) * size;

        List<Map<String, Object>> list = dao.findPurchaseHistoryPage(userId, offset, size);

        for (Map<String, Object> row : list) {
            Object midObj = row.get("mainMusicId");

            row.putIfAbsent("albumImageUrl", null);
            row.putIfAbsent("albumName", null);
            row.putIfAbsent("artistName", null);
            row.putIfAbsent("musicName", null);

            String fallbackTitle = (midObj == null || String.valueOf(midObj).isBlank())
                    ? "-" : String.valueOf(midObj);
            row.put("titleSummary", fallbackTitle); 

            if (midObj == null) continue;

            String normalizedId = normalizeSpotifyTrackId(String.valueOf(midObj));
            if (normalizedId == null) continue;

            try {
                TrackResponseV1 tr = spotifyMusicService.getTrackResponseById(normalizedId);

                String trackName = (tr != null ? tr.getTrackName() : null);
                String albumName = (tr != null && tr.getAlbum() != null) ? tr.getAlbum().getAlbumName() : null;
                String albumImageUrl = (tr != null && tr.getAlbum() != null) ? tr.getAlbum().getAlbumImageUrl() : null;

                String artistName = null;
                if (tr != null && tr.getArtist() != null) {
                    artistName = tr.getArtist().stream()
                            .map(com.github.musicsnsproject.web.dto.music.spotify.artist.SimplifiedArtist::artistName)
                            .filter(java.util.Objects::nonNull)
                            .distinct()
                            .collect(java.util.stream.Collectors.joining(", "));
                }

                row.put("musicName", trackName);
                row.put("albumName", albumName);
                row.put("albumImageUrl", albumImageUrl);
                row.put("artistName", artistName);

                Integer musicCount = null;
                Object mcObj = row.get("musicCount");
                if (mcObj instanceof Number) {
                    musicCount = ((Number) mcObj).intValue();
                } else if (mcObj != null) {
                    try { musicCount = Integer.parseInt(String.valueOf(mcObj)); } catch (Exception ignore) {}
                }
                String title = (trackName == null || trackName.isBlank()) ? fallbackTitle : trackName;
                if (musicCount != null && musicCount > 1) {
                    title = title + " 외 " + (musicCount - 1) + "건";
                }
                row.put("titleSummary", title);

            } catch (Exception ignore) {
            	
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        map.put("list", list); 
        map.put("totalCount", totalCount);
        map.put("totalPage", totalPage);
        map.put("page", current);
        map.put("size", size);
        map.put("pageBar", makePageBar(totalCount, size, current, "/api/mypage/eumpyo/history/purchase"));

        return map;
    }

    
    // purchase_history_id 기준으로 그룹 메서드
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> groupByPurchase(List<Map<String, Object>> rows) {
    	
        Map<Object, Map<String, Object>> grouped = new LinkedHashMap<>();
        
        for (Map<String, Object> row : rows) {
        	
            Object pid = row.get("purchaseHistoryId");
            
            Map<String, Object> bucket = grouped.computeIfAbsent(pid, k -> {
            	
                Map<String, Object> map = new LinkedHashMap<>();
                
                map.put("purchaseHistoryId", pid);
                map.put("purchasedAt", row.get("purchasedAt"));
                map.put("usedCoin", row.get("usedCoin"));
                map.put("coinBalance", row.get("coinBalance"));
                map.put("tracks", new ArrayList<Map<String, Object>>());
                
                return map;
            });
            
            Map<String, Object> track = new LinkedHashMap<>();
            
            track.put("musicId", row.get("musicId"));
            track.put("musicName", row.get("musicName"));
            track.put("albumName", row.get("albumName"));
            track.put("albumImageUrl", row.get("albumImageUrl"));
            track.put("artistName", row.get("artistName"));
            ((List<Map<String, Object>>) bucket.get("tracks")).add(track);
        }
        return new ArrayList<>(grouped.values());
    }

    
    // 페이지바
    private String makePageBar(int totalCount, int sizePerPage, int currentShowPageNo, String baseUrl) {
        int totalPage = (int) Math.ceil((double) totalCount / sizePerPage); // 총 페이지
        if (totalPage == 0) totalPage = 1; // 최소 1페이지

        int blockSize = 10;
        int loop = 1;
        int pageNo = ((currentShowPageNo - 1) / blockSize) * blockSize + 1;

        StringBuilder pageBar = new StringBuilder("<ul style='list-style:none;'>");

        // 맨처음
        pageBar.append("<li style='display:inline-block; width:70px; font-size:12pt;'><a href='")
               .append(baseUrl).append("?page=1&size=").append(sizePerPage)
               .append("'>[맨처음]</a></li>");
        
        // 이전
        if (pageNo != 1) {
            pageBar.append("<li style='display:inline-block; width:50px; font-size:12pt;'><a href='")
                   .append(baseUrl).append("?page=").append(pageNo - 1).append("&size=").append(sizePerPage)
                   .append("'>[이전]</a></li>");
        }
        
        // 숫자 페이지
        while (!(loop > blockSize || pageNo > totalPage)) {
            if (pageNo == currentShowPageNo) {
                pageBar.append("<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>")
                       .append(pageNo).append("</li>");
            } else {
                pageBar.append("<li style='display:inline-block; width:30px; font-size:12pt;'><a href='")
                       .append(baseUrl).append("?page=").append(pageNo).append("&size=").append(sizePerPage)
                       .append("'>").append(pageNo).append("</a></li>");
            }
            loop++; pageNo++;
        }

        // 다음
        if (pageNo <= totalPage) {
            pageBar.append("<li style='display:inline-block; width:50px; font-size:12pt;'><a href='")
                   .append(baseUrl).append("?page=").append(pageNo).append("&size=").append(sizePerPage)
                   .append("'>[다음]</a></li>");
        }
        
        // 마지막
        pageBar.append("<li style='display:inline-block; width:70px; font-size:12pt;'><a href='")
               .append(baseUrl).append("?page=").append(totalPage).append("&size=").append(sizePerPage)
               .append("'>[마지막]</a></li>");
        pageBar.append("</ul>");

        return pageBar.toString();
    }

    // Spotify Track ID 정규화
    private static final Pattern SPOTIFY_TRACK_ID = Pattern.compile("([A-Za-z0-9]{22})");

    private String normalizeSpotifyTrackId(String raw) {
    	
        if (raw == null || raw.isBlank()) return null;

        int idx = raw.indexOf("/track/");
        if (idx >= 0) {
        	
            String after = raw.substring(idx + "/track/".length());
            Matcher m = SPOTIFY_TRACK_ID.matcher(after);
            
            if (m.find()) return m.group(1);
        }

        int uriIdx = raw.indexOf("spotify:track:");
        if (uriIdx >= 0) {
            String after = raw.substring(uriIdx + "spotify:track:".length());
            Matcher m = SPOTIFY_TRACK_ID.matcher(after);
            if (m.find()) return m.group(1);
        }

        Matcher m = SPOTIFY_TRACK_ID.matcher(raw); 
        if (m.find()) return m.group(1);

        return null; 
    }
}
