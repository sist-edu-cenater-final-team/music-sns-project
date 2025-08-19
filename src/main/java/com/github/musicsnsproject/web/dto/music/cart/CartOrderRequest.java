package com.github.musicsnsproject.web.dto.music.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartOrderRequest {
    private List<Long> cartIdList;
}
