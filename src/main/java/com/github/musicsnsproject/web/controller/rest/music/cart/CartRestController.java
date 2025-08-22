package com.github.musicsnsproject.web.controller.rest.music.cart;


import com.github.musicsnsproject.service.music.cart.CartService;
import com.github.musicsnsproject.web.dto.music.cart.CartDeleteRequest;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;

    //TODO 로그인 안한 경우를 대비하여 기본값 설정 추후 제거 예정
    private static final Long defaultUserId = 23L; // 임시로 설정한 기본 사용자 ID

    private Long uid(@AuthenticationPrincipal Long userId) {
        return Objects.requireNonNullElse(userId, defaultUserId);
        // userId가 null인 경우 defaultUserId를 반환
    }

    // 장바구니 리스트
    @GetMapping("list")
    public ResponseEntity<List<CartResponse>> getCartList(@AuthenticationPrincipal Long userId){

        List<CartResponse> cartList = cartService.getCartList(uid(userId));
        return ResponseEntity.ok(cartList);
    }

    // 장바구니 추가
    @PostMapping("add")
    public ResponseEntity<List<CartResponse>> addCart(@AuthenticationPrincipal Long userId,
                                                      @RequestParam("trackId") String trackId){

        List<CartResponse> responseList = cartService.addCart(uid(userId), trackId);
        return ResponseEntity.ok(responseList);
    }

    // 장바구니 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<List<CartResponse>> deleteCart(@AuthenticationPrincipal Long userId,
                                                         @RequestBody CartDeleteRequest cartDeleteRequest){

        // 상품 삭제하기
        List<Long> cartIdList = cartDeleteRequest.getCartIdList();
        cartService.deleteCart(uid(userId), cartIdList);

        // 삭제된 상품 포함하여 최신 리스트 가져오기
        List<CartResponse> updatedList = cartService.getCartList(uid(userId));


        return ResponseEntity.ok(updatedList);
    }

}
