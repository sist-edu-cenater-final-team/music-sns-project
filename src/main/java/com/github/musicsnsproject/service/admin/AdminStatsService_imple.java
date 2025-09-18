package com.github.musicsnsproject.service.admin;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.github.musicsnsproject.repository.mybatis.dao.admin.AdminStatsDAO;
import lombok.RequiredArgsConstructor;

// 스포티파이 메타 병합용
import java.util.LinkedHashMap;
import com.github.musicsnsproject.service.music.SpotifyMusicService;
import com.github.musicsnsproject.web.dto.music.spotify.track.TrackResponseV1;

@Service
@RequiredArgsConstructor
public class AdminStatsService_imple implements AdminStatsService {

	private final AdminStatsDAO dao;
	private final SpotifyMusicService spotifyMusicService;

	// 기간 내 충전 음표 합계 조회
	@Override
	public Long sumChargedCoin(Map<String, String> param) {
		return dao.sumChargedCoin(param);
	}

	// 기간 내 매출 합계 조회
	@Override
	public Long sumRevenue(Map<String, String> param) {
		return dao.sumRevenue(param);
	}

	// 기간 내 사용 음표 합계 조회
	@Override
	public Long sumUsedCoin(Map<String, String> param) {
		return dao.sumUsedCoin(param);
	}

	// 일자별 충전 음표
	@Override
	public List<Map<String, Object>> seriesChargedCoin(Map<String, String> param) {
		return dao.seriesChargedCoin(param);
	}

	// 일자별 사용 음표
	@Override
	public List<Map<String, Object>> seriesUsedCoin(Map<String, String> param) {
		return dao.seriesUsedCoin(param);
	}

	// 음표 충전 Top 10
	@Override
	public List<Map<String, Object>> topChargers(Map<String, String> param) {
		return dao.topChargers(param);
	}

	// 음표 사용 Top 10
	@Override
	public List<Map<String, Object>> topSpenders(Map<String, String> param) {
		return dao.topSpenders(param);
	}

	// 일자별 신규 가입자
	@Override
	public List<Map<String, Object>> seriesNewMembers(Map<String, String> param) {
		return dao.seriesNewMembers(param);
	}

	// 팔로워 Top 10
	@Override
	public List<Map<String, Object>> topFollowers(Map<String, String> param) {
		return dao.topFollowers(param);
	}

	// 음악 판매/음표 Top 10
	@Override
	public List<Map<String, Object>> topMusic(Map<String, String> param) {
		
		List<Map<String, Object>> rows = dao.topMusic(param);
		
		if (rows == null || rows.isEmpty())
			return rows;

		Map<String, Map<String, Object>> map = new LinkedHashMap<>();
		
		for (Map<String, Object> row : rows) {
			Object key = (row.get("musicid") != null) ? row.get("musicid") : row.get("musicId");
			String id = key == null ? null : String.valueOf(key);
			
			if (id != null) {
				
				row.put("musicid", id); 
				map.put(id, row);
			}
		}

		for (String id : map.keySet()) {
			
			try {
				TrackResponseV1 tr = spotifyMusicService.getTrackResponseById(id);
				if (tr == null) continue;

				String title = tr.getTrackName();
				String album = tr.getAlbum() != null ? tr.getAlbum().getAlbumName() : null;
				String image = tr.getAlbum() != null ? tr.getAlbum().getAlbumImageUrl() : null;

				String artist = null;
				if (tr.getArtist() != null) {
					
					StringBuilder sb = new StringBuilder();
					
					for (var a : tr.getArtist()) {
						if (a == null || a.artistName() == null) continue;
						if (sb.length() > 0) sb.append(", ");
						sb.append(a.artistName());
					}
					artist = sb.toString();
				}

				Map<String, Object> row = map.get(id);
				
				row.put("title", title);
				row.put("artist", artist);
				row.put("album", album);
				row.put("imageUrl", image);
				
				if (!row.containsKey("musicName") && title != null) row.put("musicName", title);
				if (!row.containsKey("artistName") && artist != null) row.put("artistName", artist);
				
			} catch (Exception ignore) {
			}
		}
		return rows;
	}

	// 일자별 수익
	@Override
	public List<Map<String, Object>> seriesRevenue(Map<String, String> param) {
		return dao.seriesRevenue(param);
	}

	// 시간별 방문자
	@Override
	public List<Map<String, Object>> seriesHourlyVisitors(Map<String, String> param) {
		return dao.seriesHourlyVisitors(param);
	}

	// 일자별 방문자
	@Override
	public List<Map<String, Object>> seriesDailyVisitors(Map<String, String> param) {
		return dao.seriesDailyVisitors(param);
	}

	// 전체 이용자 수
	@Override
	public Long countTotalUsers() {
		return dao.countTotalUsers();
	}
}
