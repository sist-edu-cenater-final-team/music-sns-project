package com.github.musicsnsproject.service.mypage.eumpyo;

import com.github.musicsnsproject.repository.mybatis.dao.eumpyo.EumpyoHistoryDAO;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.dto.music.spotify.artist.SimplifiedArtist;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponseV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;   
import java.util.regex.Pattern;   

@Service
@RequiredArgsConstructor
public class EumpyoHistoryService_imple implements EumpyoHistoryService {

    private final EumpyoHistoryDAO dao;
    private final SpotifyMusicService spotifyMusicService;

    // 충전내역
    @Override
    public Map<String, Object> getChargeHistory(long userId, int page, int size) {
    	
        size = Math.max(1, size);

        int totalCount = dao.countChargeHistory(userId);
        int totalPage  = (int) Math.ceil((double) totalCount / size);
        int current    = Math.max(1, Math.min(page, Math.max(totalPage, 1)));
        int offset     = (current - 1) * size;

        List<Map<String, Object>> list = dao.findChargeHistoryPage(userId, offset, size);

        Map<String, Object> out = new HashMap<>();
        
        out.put("result", "success");
        out.put("list", list);
        out.put("totalCount", totalCount);
        out.put("totalPage", totalPage);
        out.put("page", current);
        out.put("size", size);
        out.put("pageBar", makePageBar(totalCount, size, current, "/api/mypage/eumpyo/history/charge"));

        return out;
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
        	
            Object midObj = row.get("musicId");
            
            if (midObj == null) {
                row.put("musicName", null);
                row.put("albumName", null);
                row.put("albumImageUrl", null);
                row.put("artistName", null);
                continue;
            }

            String normalizedId = normalizeSpotifyTrackId(String.valueOf(midObj));
            
            if (normalizedId == null) { 
                row.put("musicName", null);
                row.put("albumName", null);
                row.put("albumImageUrl", null);
                row.put("artistName", null);
                
                continue;
            }

            try {
                TrackResponseV1 tr = spotifyMusicService.getTrackResponseById(normalizedId);

                row.put("musicName", tr != null ? tr.getTrackName() : null);

                String albumName = (tr != null && tr.getAlbum() != null) ? tr.getAlbum().getAlbumName() : null;
                
                row.put("albumName", albumName);

                String albumImageUrl = (tr != null && tr.getAlbum() != null) ? tr.getAlbum().getAlbumImageUrl() : null;
                
                row.put("albumImageUrl", albumImageUrl);

                String artistName = null;
                
                if (tr != null && tr.getArtist() != null) {
                	
                    artistName = tr.getArtist().stream()
                            .map(SimplifiedArtist::artistName)
                            .filter(java.util.Objects::nonNull)
                            .distinct()
                            .collect(java.util.stream.Collectors.joining(", "));
                }
                
                row.put("artistName", artistName);

            } catch (Exception e) {
            	
                row.put("musicName", null);
                row.put("albumName", null);
                row.put("albumImageUrl", null);
                row.put("artistName", null);
            }
        }

        Map<String, Object> out = new HashMap<>();
        
        out.put("result", "success");
        out.put("list", list);
        out.put("totalCount", totalCount);
        out.put("totalPage", totalPage);
        out.put("page", current);
        out.put("size", size);
        out.put("pageBar", makePageBar(totalCount, size, current, "/api/mypage/eumpyo/history/purchase"));

        return out;
    }

    
    // 페이지바
    private String makePageBar(int totalCount, int sizePerPage, int currentShowPageNo, String baseUrl) {
        int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);
        if (totalPage == 0) totalPage = 1;

        int blockSize = 10;
        int loop = 1;
        int pageNo = ((currentShowPageNo - 1) / blockSize) * blockSize + 1;

        StringBuilder pageBar = new StringBuilder("<ul style='list-style:none;'>");

        pageBar.append("<li style='display:inline-block; width:70px; font-size:12pt;'><a href='")
               .append(baseUrl).append("?page=1&size=").append(sizePerPage)
               .append("'>[맨처음]</a></li>");
        if (pageNo != 1) {
            pageBar.append("<li style='display:inline-block; width:50px; font-size:12pt;'><a href='")
                   .append(baseUrl).append("?page=").append(pageNo - 1).append("&size=").append(sizePerPage)
                   .append("'>[이전]</a></li>");
        }

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

        if (pageNo <= totalPage) {
            pageBar.append("<li style='display:inline-block; width:50px; font-size:12pt;'><a href='")
                   .append(baseUrl).append("?page=").append(pageNo).append("&size=").append(sizePerPage)
                   .append("'>[다음]</a></li>");
        }
        pageBar.append("<li style='display:inline-block; width:70px; font-size:12pt;'><a href='")
               .append(baseUrl).append("?page=").append(totalPage).append("&size=").append(sizePerPage)
               .append("'>[마지막]</a></li>");
        pageBar.append("</ul>");

        return pageBar.toString();
    }

   
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
