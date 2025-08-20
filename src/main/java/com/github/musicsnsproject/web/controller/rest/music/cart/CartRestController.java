package com.github.musicsnsproject.web.controller.rest.music.cart;


import com.github.musicsnsproject.service.music.cart.CartService;
import com.github.musicsnsproject.web.dto.music.cart.CartDeleteRequest;
import com.github.musicsnsproject.web.dto.music.cart.CartOrderRequest;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;

    // 장바구니 리스트
    @GetMapping("list")
    public ResponseEntity<List<CartResponse>> getCartList(@AuthenticationPrincipal Long userId){

        // 로그인 안한 경우를 대비하여 기본값 설정
        userId = userId == null ? 23 : userId;

        List<CartResponse> cartList = cartService.getCartList(userId);
        return ResponseEntity.ok(cartList);
    }

    // 장바구니 추가
    @PostMapping("add")
    public ResponseEntity<List<CartResponse>> addCart(@AuthenticationPrincipal Long userId,
                                                      @RequestParam("trackId") String trackId){

        // 로그인 안한 경우를 대비하여 기본값 설정
        userId = userId == null ? 23 : userId;

        List<CartResponse> responseList = cartService.addCart(userId, trackId);
        return ResponseEntity.ok(responseList);
    }

    // 장바구니 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<List<CartResponse>> deleteCart(@AuthenticationPrincipal Long userId,
                                                         @RequestBody CartDeleteRequest cartDeleteRequest){


        // 로그인 안한 경우를 대비하여 기본값 설정
        userId = userId == null ? 23 : userId;

        // 상품 삭제하기
        List<Long> cartIdList = cartDeleteRequest.getCartIdList();
        cartService.deleteCart(userId, cartIdList);

        // 삭제된 상품 포함하여 최신 리스트 가져오기
        List<CartResponse> updatedList = cartService.getCartList(userId);


        return ResponseEntity.ok(updatedList);
    }

    // 주문하기
    @PostMapping("order")
    public ResponseEntity<List<CartResponse>> orderCart(@AuthenticationPrincipal Long userId,
                                                        @RequestParam("cartIdList") List<Long> cartIdList){

        // 로그인 안한 경우를 대비하여 기본값 설정
        userId = userId == null ? 23 : userId;

        return ResponseEntity.ok(cartService.getCartOrderList(userId, cartIdList));
    }

    // 주문 정보 가져오기
    @GetMapping("/order")
    public ResponseEntity<List<CartResponse>> orderCartInfo(@AuthenticationPrincipal Long userId,
                                                            @RequestParam("cartIdList") List<Long> cartIdList) {

        // 로그인 안한 경우를 대비하여 기본값 설정
        userId = userId == null ? 23 : userId;

        return ResponseEntity.ok(cartService.getCartOrderList(userId, cartIdList));
    }

}
