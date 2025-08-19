package com.github.musicsnsproject.service.music.cart;

import com.github.musicsnsproject.web.dto.music.cart.CartResponse;

import java.util.List;

public interface CartService {

    // 장바구니 리스트
    List<CartResponse> getCartList(Long userId);

    // 장바구니 담기
    List<CartResponse> addCart(Long userId, String trackId);

    // 장바구니 삭제하기
    void deleteCart(Long userId, List<Long> cartIdList);

    // 주문하기
    List<CartResponse> getCartOrderList(Long userId, List<Long> cartIdList);
}
