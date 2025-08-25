package com.github.musicsnsproject.web.controller.rest.music.order;

import com.github.musicsnsproject.common.exceptions.CustomNotAcceptException;
import com.github.musicsnsproject.service.music.order.OrderService;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    //TODO 로그인 안한 경우를 대비하여 기본값 설정 추후 제거 예정
    private static final Long defaultUserId = 23L; // 임시로 설정한 기본 사용자 ID


    // 주문 미리보기
    @GetMapping
    public ResponseEntity<List<CartResponse>> orderPreview(@AuthenticationPrincipal Long userId,
                                                           @RequestParam("cartIdList") List<Long> cartIdList) {

        return ResponseEntity.ok(orderService.getOrderPreviewList(userId, cartIdList));
    }

    // 주문 생성하기
    @PostMapping("/create")
    public ResponseEntity<List<CartResponse>> orderCreate(@AuthenticationPrincipal Long userId,
                                                         @RequestParam("cartIdList") List<Long> cartIdList){

        return ResponseEntity.ok(orderService.getOrderPreviewList(userId, cartIdList));
    }

    // 주문 확정하기
    @PostMapping("confirm")
    public ResponseEntity<?> orderConfirm(@AuthenticationPrincipal Long userId,
                                          @RequestParam("cartIdList") List<Long> cartIdList) {

        orderService.orderConfirm(userId, cartIdList);

        return ResponseEntity.ok("주문이 성공적으로 완료되었습니다.");
    }
}
