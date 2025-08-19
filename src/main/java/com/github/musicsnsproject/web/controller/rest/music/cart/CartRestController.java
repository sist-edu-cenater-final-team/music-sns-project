package com.github.musicsnsproject.web.controller.rest.music.cart;


import com.github.musicsnsproject.service.music.cart.CartService;
import com.github.musicsnsproject.web.dto.music.cart.CartDeleteRequest;
import com.github.musicsnsproject.web.dto.music.cart.CartOrderRequest;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;

    // 장바구니 리스트
    @GetMapping("list")
    public ResponseEntity<List<CartResponse>> getCartList(@RequestParam("userId") Long userId){

        List<CartResponse> cartList = cartService.getCartList(userId);
        return ResponseEntity.ok(cartList);
    }

    // 장바구니 추가
    @PostMapping("add")
    public ResponseEntity<List<CartResponse>> addCart(@RequestParam("userId") Long userId,
                                                      @RequestParam("trackId") String trackId){

        List<CartResponse> responseList = cartService.addCart(userId, trackId);
        return ResponseEntity.ok(responseList);
    }

    // 장바구니 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<List<CartResponse>> deleteCart(@RequestBody CartDeleteRequest cartDeleteRequest){

        // 상품 삭제하기
        Long userId = cartDeleteRequest.getUserId();
        List<Long> cartIdList = cartDeleteRequest.getCartIdList();
        cartService.deleteCart(userId, cartIdList);

        // 삭제된 상품 포함하여 최신 리스트 가져오기
        List<CartResponse> updatedList = cartService.getCartList(userId);

        return ResponseEntity.ok(updatedList);
    }

    // 주문하기
    @PostMapping("order")
    public ResponseEntity<List<CartResponse>> orderCart(@RequestBody CartOrderRequest cartOrderRequest){
        Long userId = cartOrderRequest.getUserId();
        List<Long> cartIdList = cartOrderRequest.getCartIdList();

        return ResponseEntity.ok(cartService.getCartOrderList(userId, cartIdList));
    }

    // 주문 정보 가져오기
    @GetMapping("/order/{userId}")
    public ResponseEntity<List<CartResponse>> orderCartInfo(@PathVariable("userId") Long userId,
                                                            @RequestParam("cartIdList") List<Long> cartIdList) {

        return ResponseEntity.ok(cartService.getCartOrderList(userId, cartIdList));
    }

}
