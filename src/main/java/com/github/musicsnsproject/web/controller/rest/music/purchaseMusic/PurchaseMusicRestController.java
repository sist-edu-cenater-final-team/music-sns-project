package com.github.musicsnsproject.web.controller.rest.music.purchaseMusic;

import com.github.musicsnsproject.service.music.purchase.PurchaseMusicService;
import com.github.musicsnsproject.web.dto.music.purchase.PurchaseMusicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/purchaseMusic")
@RequiredArgsConstructor
public class PurchaseMusicRestController {

    private final PurchaseMusicService purchaseMusicService;

    @GetMapping("list")
    ResponseEntity<PurchaseMusicResponse> getPurchaseMusicList(@AuthenticationPrincipal Long userId,
                                                               @RequestParam(value = "pageNo", defaultValue = "1") int currentShowPageNo){

        return ResponseEntity.ok(purchaseMusicService.getPurchaseMusicList(userId, currentShowPageNo));
    }
}
