package com.github.musicsnsproject.web.controller.rest.user;

import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 인증 객체에서 사용자 ID 추출
    private Long getUserId(Object principal) {
        if (principal == null) return null;
        if (principal instanceof Long) return (Long) principal;
        if (principal instanceof CustomUserDetails cud) return cud.getUserId();

        var ctx = org.springframework.security.core.context.SecurityContextHolder.getContext();
        if (ctx != null && ctx.getAuthentication() != null) {
            Object p = ctx.getAuthentication().getPrincipal();
            if (p instanceof CustomUserDetails cud2) return cud2.getUserId();
        }
        return null;
    }

    // 알림 페이지 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@AuthenticationPrincipal Object principal,
                                                    @RequestParam(defaultValue = "1")  int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        Long userId = getUserId(principal);
        if (userId == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("result", "fail");
            map.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }
        return ResponseEntity.ok(notificationService.getNotificationPage(userId, page, size));
    }

    // 모두 읽음 처리
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal Object principal) {
        Long userId = getUserId(principal);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // 개별 알림 삭제
    @DeleteMapping("/one")
    public ResponseEntity<Void> deleteOne(@AuthenticationPrincipal Object principal,
                                          @RequestParam("eventKey") String eventKey) {
        Long userId = getUserId(principal);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        notificationService.deleteOne(userId, eventKey);
        return ResponseEntity.ok().build();
    }

    // 전체 알림 삭제
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAll(@AuthenticationPrincipal Object principal) {
        Long userId = getUserId(principal);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        notificationService.deleteAll(userId);
        return ResponseEntity.ok().build();
    }

    // 개별 알림 읽음 처리
    @PostMapping("/read-one")
    public ResponseEntity<Void> markOneRead(@AuthenticationPrincipal Object principal,
                                            @RequestParam("eventKey") String eventKey) {
        Long userId = getUserId(principal);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        notificationService.markOneAsRead(userId, eventKey);
        return ResponseEntity.ok().build();
    }
}
