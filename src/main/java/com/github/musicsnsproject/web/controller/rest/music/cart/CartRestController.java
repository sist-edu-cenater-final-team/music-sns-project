package com.github.musicsnsproject.web.controller.rest.music.cart;


import com.github.musicsnsproject.service.music.cart.CartService;
import com.github.musicsnsproject.web.dto.music.cart.CartResponse;
import com.github.musicsnsproject.web.dto.response.CustomErrorResponse;
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

        System.out.println(userId);

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

//    // 장바구니 삭제
//    @DeleteMapping("delete")
//    public ResponseEntity<List<CartResponse>> deleteCart(@RequestBody CartResponse cartResponse){
//
//        Long aa = cartResponse.getCartId();
//        // 상품 삭제하기
//        cartService.deleteCart(cartResponse.getUserId(), cartResponse.getCartId());
//
//        // 삭제된 상품 포함하여 최신 리스트 가져오기
//        List<CartResponse> updatedList = cartService.getCartList(cartResponse.getUserId());
//
//        return ResponseEntity.ok(updatedList);
//    }
    // 장바구니 삭제
    @DeleteMapping("delete")
    public ResponseEntity<List<CartResponse>> deleteCart(@RequestBody Long userId,
                                                         @RequestBody List<Long> cartIdList){

        // 상품 삭제하기
        cartService.deleteCart(userId, cartIdList);

        // 삭제된 상품 포함하여 최신 리스트 가져오기
        List<CartResponse> updatedList = cartService.getCartList(userId);

        return ResponseEntity.ok(updatedList);
    }

}
