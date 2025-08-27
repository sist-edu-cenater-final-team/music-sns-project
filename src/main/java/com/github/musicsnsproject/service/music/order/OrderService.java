package com.github.musicsnsproject.service.music.order;

import com.github.musicsnsproject.web.dto.music.cart.CartResponse;

import java.util.List;

public interface OrderService {
    // 주문 미리보기
    List<CartResponse> getOrderPreviewList(Long userId, List<Long> cartIdList);

    // 주문 확정하기
    void orderConfirm(Long userId, List<Long> cartIdList);

    // 사용자의 보유코인 알아오기
    void checkCoin(Long userId, List<Long> cartIdList);
}
