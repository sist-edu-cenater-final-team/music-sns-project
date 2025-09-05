package com.github.musicsnsproject.service.notification;

import com.github.musicsnsproject.repository.mybatis.dao.notification.NotificationDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationService_imple implements NotificationService {

    private final NotificationDAO notificationDAO;
    private final RedisTemplate<byte[], byte[]> redisTemplate;

    // 시간대 및 표시형식
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter FMT_TODAY = DateTimeFormatter.ofPattern("HH:mm"); // 오늘: 시:분
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 이전: 날짜

    // 문자열 <-> 바이트 (Redis 저장용)
    private static byte[] toBytes(String s) {
    	return s.getBytes(StandardCharsets.UTF_8);
    }
    
    private static String toString(byte[] b) {
    	return b == null ? null : new String(b, StandardCharsets.UTF_8);
    }

    // 사용자별 Redis 키
    private static byte[] keyLastRead(long userId) { // 마지막 "모두 읽음" 시간
    	return toBytes("noti:lastRead:" + userId);
    }       
    
    private static byte[] keyHiddenBefore(long userId) { // "전체 삭제" 기준 시간
    	return toBytes("noti:hiddenBefore:" + userId);
    }   
    
    private static byte[] keyDismissed(long userId) { // 개별 삭제된 알림 집합
    	return toBytes("noti:dismissed:" + userId);
    }      
    
    private static byte[] keyRead(long userId) { // 개별 읽음된 알림 집합
    	return toBytes("noti:read:" + userId);
    }

    private static long toLongOrZero(String text) {
        if (text == null || text.isBlank()) return 0L;
        
        try { return Long.parseLong(text);
        
        } catch (Exception ignore) {
        	return 0L;
        }
    }

    // Map에서 값 꺼내기
    private static String getColString(Map<String, Object> row, String col) {
    	
        Object obj = row.get(col);
        
        if (obj == null) obj = row.get(col.toUpperCase(Locale.ROOT));
        
        return obj == null ? null : String.valueOf(obj);
    }
    
    private static Long getColLong(Map<String, Object> row, String col) {
    	
        Object obj = row.get(col);
        
        if (obj == null) obj = row.get(col.toUpperCase(Locale.ROOT));
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        
        try {
        	return Long.parseLong(String.valueOf(obj));
        	
        	} catch (Exception ignore) {
        		return null;
        	}
    }
    private static Date getColDate(Map<String, Object> row, String col) {
    	
        Object obj = row.get(col);
        
        if (obj == null) obj = row.get(col.toUpperCase(Locale.ROOT));
        if (obj instanceof Date) return (Date) obj;
        if (obj instanceof java.sql.Timestamp) return new Date(((java.sql.Timestamp) obj).getTime());
        if (obj instanceof java.sql.Date) return new Date(((java.sql.Date) obj).getTime());
        
        return null;
    }

    // 알림 고유키 만들기 (종류+작성자+글/댓글+시간)
    private static String buildEventKey(Map<String, Object> row) {
    	
        String eventType = getColString(row, "event_type");
        Long actorUserId = getColLong(row, "actor_user_id");
        Long postId = getColLong(row, "post_id");
        Long commentId = getColLong(row, "comment_id");
        Date createdAtDate = getColDate(row, "created_at");
        
        long createdMs = (createdAtDate == null) ? 0L : createdAtDate.getTime();

        if ("FOLLOW".equals(eventType)) return "FOLLOW|" + actorUserId + "|" + createdMs;
        if ("LIKE".equals(eventType)) return "LIKE|" + actorUserId + "|" + postId + "|" + createdMs;
        if ("COMMENT".equals(eventType)) return "COMMENT|" + actorUserId + "|" + postId + "|" + commentId + "|" + createdMs;
        
        return "UNKNOWN|" + createdMs;
    }

    // 알림 페이지 조회
    @Override
    public Map<String, Object> getNotificationPage(long recipientUserId, int pageNumber, int pageSize) {

        int safePageNumber = Math.max(1, pageNumber);
        int safePageSize = Math.max(1, pageSize);
        int startIndex = (safePageNumber - 1) * safePageSize;
        int endIndex = startIndex + safePageSize;

        // DB에서 알림 읽기
        Map<String, Object> map = new HashMap<>();
        
        map.put("recipientUserId", recipientUserId);
        
        List<Map<String, Object>> followEventRows = notificationDAO.listFollows(map); // 팔로우
        List<Map<String, Object>> likeEventRows = notificationDAO.listLikes(map); // 좋아요
        List<Map<String, Object>> commentEventRows = notificationDAO.listComments(map); // 댓글

        // 세 목록 합치기
        List<Map<String, Object>> mergedEventRows = new ArrayList<>();
        
        if (followEventRows != null) mergedEventRows.addAll(followEventRows);
        if (likeEventRows != null) mergedEventRows.addAll(likeEventRows);
        if (commentEventRows != null) mergedEventRows.addAll(commentEventRows);

        // 최신순 정렬
        mergedEventRows.sort((a, b) -> {
        	
            Date aCreated = getColDate(a, "created_at");
            Date bCreated = getColDate(b, "created_at");
            
            long aMs = (aCreated == null) ? Long.MIN_VALUE : aCreated.getTime();
            long bMs = (bCreated == null) ? Long.MIN_VALUE : bCreated.getTime();
            
            return Long.compare(bMs, aMs);
        });

        // Redis에서 상태 읽기
        long hiddenBeforeMs = toLongOrZero(toString(redisTemplate.opsForValue().get(keyHiddenBefore(recipientUserId))));
        long lastReadMs = toLongOrZero(toString(redisTemplate.opsForValue().get(keyLastRead(recipientUserId))));

        Set<byte[]> dismissedBytes = redisTemplate.opsForSet().members(keyDismissed(recipientUserId));
        Set<String> dismissedEventKeys = new HashSet<>();
        
        if (dismissedBytes != null) {
            for (byte[] raw : dismissedBytes) {
                String key = toString(raw);
                if (key != null && !key.isBlank()) dismissedEventKeys.add(key);
            }
        }
        
        Set<byte[]> readBytes = redisTemplate.opsForSet().members(keyRead(recipientUserId));
        Set<String> readEventKeys = new HashSet<>();
        if (readBytes != null) {
            for (byte[] raw : readBytes) {
                String key = toString(raw);
                if (key != null && !key.isBlank()) readEventKeys.add(key);
            }
        }

        // 오늘 00:00
        long todayStartMs = LocalDate.now(ZONE_ID).atStartOfDay(ZONE_ID).toInstant().toEpochMilli();

        List<Map<String, Object>> dtoList = new ArrayList<>();
        
        int unreadCount = 0;

        for (Map<String, Object> row : mergedEventRows) {
        	
            Date createdAt = getColDate(row, "created_at");
            
            if (createdAt == null) continue;

            long createdAtMs = createdAt.getTime();

            // 전체 삭제 이전은 숨김
            if (hiddenBeforeMs > 0 && createdAtMs < hiddenBeforeMs) continue;

            String eventKey = buildEventKey(row);
            // 개별 삭제면 숨김
            if (dismissedEventKeys.contains(eventKey)) continue;

            // 시간 표시값
            String timeLabel = (createdAtMs >= todayStartMs)
                    ? createdAt.toInstant().atZone(ZONE_ID).toLocalDateTime().format(FMT_TODAY)
                    : createdAt.toInstant().atZone(ZONE_ID).toLocalDateTime().format(FMT_DATE);

            Map<String, Object> item = new LinkedHashMap<>();
            
            item.put("eventType", getColString(row, "event_type"));
            item.put("actorUserId", getColLong(row, "actor_user_id"));
            item.put("actorNickname", getColString(row, "actor_nickname"));
            item.put("actorProfileImage", getColString(row, "actor_profile_image"));
            item.put("recipientUserId", getColLong(row, "recipient_user_id"));
            item.put("postId", getColLong(row, "post_id"));
            item.put("commentId", getColLong(row, "comment_id"));
            item.put("createdAt", createdAtMs);
            item.put("timeLabel", timeLabel);
            item.put("eventKey", eventKey);

            boolean isUnread = (createdAtMs > lastReadMs) && !readEventKeys.contains(eventKey);
            item.put("isUnread", isUnread);

            dtoList.add(item);

            if (isUnread) {
                unreadCount++;
            }
        }

        // 페이지 슬라이스
        int total = dtoList.size();
        
        if (startIndex > total) startIndex = total;
        if (endIndex   > total) endIndex = total;

        List<Map<String, Object>> pageItems = dtoList.subList(startIndex, endIndex);

        // 최종 응답
        Map<String, Object> response = new LinkedHashMap<>();
        
        response.put("items", pageItems);
        response.put("unread", unreadCount);
        response.put("page", safePageNumber);
        response.put("size", safePageSize);
        
        return response;
    }

    // 모두 읽음 처리
    @Override
    public void markAllAsRead(long recipientUserId) {
        long nowMs = System.currentTimeMillis();
        redisTemplate.opsForValue().set(keyLastRead(recipientUserId), toBytes(String.valueOf(nowMs)));
    }

    // 개별 알림 삭제
    @Override
    public void deleteOne(long recipientUserId, String eventKey) {
        if (eventKey == null || eventKey.isBlank()) return;
        redisTemplate.opsForSet().add(keyDismissed(recipientUserId), toBytes(eventKey));
    }

    // 전체 알림 삭제
    @Override
    public void deleteAll(long recipientUserId) {
        long nowMs = System.currentTimeMillis();
        redisTemplate.opsForValue().set(keyHiddenBefore(recipientUserId), toBytes(String.valueOf(nowMs))); // 과거 알림 숨김
        redisTemplate.opsForValue().set(keyLastRead(recipientUserId),     toBytes(String.valueOf(nowMs))); // 배지 초기화
        redisTemplate.delete(keyDismissed(recipientUserId)); // 개별 삭제 목록 초기화
        redisTemplate.delete(keyRead(recipientUserId)); // 개별 읽음 목록 초기화
    }
    
    // 개별 알림 읽음 처리
    @Override
    public void markOneAsRead(long recipientUserId, String eventKey) {
        if (eventKey == null || eventKey.isBlank()) return;
        redisTemplate.opsForSet().add(keyRead(recipientUserId), toBytes(eventKey));
    }
}
