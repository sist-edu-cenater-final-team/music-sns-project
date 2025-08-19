package com.github.musicsnsproject.web.dto.music.cart;

import lombok.Data;
import java.util.List;

@Data
// 장바구니 삭제용
public class CartDeleteRequest {
    private List<Long> cartIdList;
}
